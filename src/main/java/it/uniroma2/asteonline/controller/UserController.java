package it.uniroma2.asteonline.controller;

import it.uniroma2.asteonline.exception.DAOException;
import it.uniroma2.asteonline.factory.ConnectionFactory;
import it.uniroma2.asteonline.model.dao.*;
import it.uniroma2.asteonline.model.domain.*;
import it.uniroma2.asteonline.utils.LoggedUser;
import it.uniroma2.asteonline.view.UserView;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class UserController implements Controller {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    public void start() {
        //carico la view
        userHomepage();
    }

    private void userHomepage() {
        boolean running = true;
        while (running) {
            int choice;
            try {
                choice = UserView.showMenu();
                switch (choice) {
                    case 1 -> visualizzaAste();
                    case 2 -> elencoArticoliAggiudicati();
                    case 3 -> elencoAstePartecipate();
                    case 4 -> mostraProfilo();
                    case 5 -> {
                        logout();
                        running = false;
                    }
                }
            } catch (IOException | SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void visualizzaAste() {
        boolean running = true;
        while (running) {
            int choice;
            try {
                choice = UserView.showVisualizzaAsteAttiveMenu();
                switch (choice) {
                    case 1 -> mostraAsteAttive();
                    case 2 -> filtroCategoria();
                    case 3 -> filtroAmministratore();
                    case 4 -> running = false;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    private void mostraAsteAttive() {
        try {
            List<Asta> asteAttive;
            //prelevo dal db le aste attive (senza alcun filtro)
            asteAttive = new VisualizzaAsteFiltrateDAO().execute(null, null);

            int choice = UserView.showAste(asteAttive);

            if (choice > asteAttive.size()) {
                //l'utente ha scelto di tornare indietro
                return;
            }

            //altrimenti mostro i dettagli di una singola asta
            Asta astaScelta = asteAttive.get(choice - 1);
            dettagliAsta(astaScelta);
        } catch (IOException | DAOException e) {
            throw new RuntimeException(e);
        }
    }

    private void filtroCategoria() {
        try {
            Categoria catTree = loadCategoriesTree();
            Categoria filtro;
            List<Asta> asteFiltrate;

            //scelgo la categoria filtro
            filtro = UserView.showFiltraAsteCatMenu(catTree, this);

            //prelevo dal db le aste con questa categoria filtro
            asteFiltrate = new VisualizzaAsteFiltrateDAO().execute(filtro, null);

            int choice = UserView.showAsteFiltrate(asteFiltrate);

            if (choice > asteFiltrate.size()) {
                //l'utente ha scelto di tornare indietro
                return;
            }

            //altrimenti mostro i dettagli di una singola asta
            Asta astaScelta = asteFiltrate.get(choice - 1);
            dettagliAsta(astaScelta);

        } catch (IOException | DAOException e) {
            throw new RuntimeException(e);
        }
    }

    private void filtroAmministratore() {
        try {
            List<Utente> adminList;
            List<Asta> asteFiltrate;
            Utente filtro;


            //prelevo dal db la lista degli utenti amministratori
            adminList = new GetAdminListDAO().execute();

            //scelgo l'amministratore da filtrare
            filtro = UserView.showFiltraAsteAdminMenu(adminList);

            //prelevo dal db le aste con questo filtro
            asteFiltrate = new VisualizzaAsteFiltrateDAO().execute(null, filtro);

            int choice = UserView.showAsteFiltrate(asteFiltrate);

            if (choice > asteFiltrate.size()) {
                //l'utente ha scelto di tornare indietro
                return;
            }

            //altrimenti mostro i dettagli di una singola asta
            Asta astaScelta = asteFiltrate.get(choice - 1);
            dettagliAsta(astaScelta);

        } catch (IOException | DAOException e) {
            throw new RuntimeException(e);
        }
    }

    private void dettagliAsta(Asta astaScelta) {
        int choice;

        try {
            choice = UserView.showDettagliAsta(astaScelta);

            if (choice == 1) {
                aggiungiOfferta(astaScelta);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Categoria loadCategoriesTree() {
        Categoria catTree;

        try {
            //carico la lista delle categorie
            List<Categoria> fullCatList = new ListaCategorieDAO().execute();

            //creo l'albero delle categorie
            catTree = getCategoriesTree(fullCatList);
        } catch (DAOException e) {
            throw new RuntimeException(e);
        }

        return catTree;
    }

    public Categoria trovaCategoriaPerNome(Categoria root, String nome) {
        if (root.getNomeCategoria().equalsIgnoreCase(nome)) return root;

        if (root.getFigli() != null) {
            for (Categoria figlio : root.getFigli()) {
                Categoria result = trovaCategoriaPerNome(figlio, nome);
                if (result != null) return result;
            }
        }
        return null;
    }

    private Categoria getCategoriesTree(List<Categoria> fullCatList) {
        //creo una hashmap che associa al nome della categoria il suo model (contenente tutte le informazioni necessarie a ricostruire l'albero)
        Map<String, Categoria> catMap = new HashMap<>();

        //aggiungo un nodo padre di livello 0 (che farà da radice generale di tutto l'albero delle categorie)
        Categoria root = new Categoria("Radice", 0, null);

        //inserisco tutte le categorie ottenute dal db nella mappa
        for (Categoria c : fullCatList) {
            catMap.put(c.getNomeCategoria(), c);
        }

        //costruisco le relazioni padre-figli a partire dalle informazioni contenute in ciascun model
        for (Categoria c : fullCatList) {
            if (c.getCategoriaSuperiore() == null) {
                root.addFiglio(c); //sono le categorie di livello 1
            } else {
                //data una categoria prendo il suo padre, se presente, e aggiungo a esso questa categoria come figlia
                Categoria padre = catMap.get(c.getCategoriaSuperiore());
                if (padre != null) {
                    padre.addFiglio(c);
                }
            }
        }

        return root; //restituisco l'albero delle categorie
    }

    private void elencoArticoliAggiudicati() {
        List<Asta> articoliAgg;

        try {
            //prelevo tutte le aste vinte dall'utente loggato
            articoliAgg = new AsteVinteUtenteDAO().execute(LoggedUser.getCF());

            int choice;
            while (true) {
                choice = UserView.showArticoliAggiudicati(articoliAgg);

                if (choice > articoliAgg.size()) {
                    //l'utente ha scelto di tornare indietro
                    break;
                }

                //altrimenti mostro i dettagli di una singola asta
                Asta astaScelta = articoliAgg.get(choice - 1);
                UserView.showDettagliAstaVinta(astaScelta);
            }

        } catch (DAOException | IOException e) {
            throw new RuntimeException(e);
        }
    }


    private void elencoAstePartecipate() {
        List<Asta> astePartecipate;

        try {
            astePartecipate = new VisualizzaAstePartecipateDAO().execute(LoggedUser.getCF());

            int choice = UserView.showAstePartecipate(astePartecipate);

            if (choice > astePartecipate.size()) {
                //l'utente ha scelto di tornare indietro
                return;
            }

            //altrimenti mostro i dettagli di una singola asta
            Asta astaScelta = astePartecipate.get(choice - 1);
            dettagliAstaPartecipata(astaScelta);
        } catch (IOException | DAOException e) {
            throw new RuntimeException(e);
        }
    }

    private void dettagliAstaPartecipata(Asta astaScelta) {
        int choice;

        try {
            choice = UserView.showDettagliAstaPartecipata(astaScelta);

            if (choice == 1) {
                aggiungiOfferta(astaScelta);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private void aggiungiOfferta(Asta astaScelta) {
        Offerta offertaManuale = new Offerta();

        try {
            offertaManuale.setAsta(astaScelta.getId());
            offertaManuale.setUtenteBase(LoggedUser.getCF());
            offertaManuale.setData(LocalDate.now());
            offertaManuale.setOra(LocalTime.now());

            UserView.showAggiungiOffertaForm(offertaManuale);

            //inserisci offerta manuale
            System.out.print("\n++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n");
            System.out.println(new AggiungiOffertaDAO().execute(offertaManuale));
            System.out.print("\n++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n");

            //esegui la logica di controfferte in un thread separato per non bloccare l'esecuzione dell'applicazione
            executor.submit(() -> gestisciRilanciAutomatici(astaScelta));

        } catch (DAOException | IOException e) {
            System.err.println("Errore nel processo di offerta: " + e.getMessage());
        }
    }

    /*

    private void gestisciRilanciAutomatici(Asta astaScelta) {
        final BigDecimal incremento = BigDecimal.valueOf(0.50);
        final int MAX_RILANCI = 100;
        int rilanciEffettuati = 0;

        try {
            while (rilanciEffettuati < MAX_RILANCI) {
                Offerta migliorOfferta = new GetMigliorOffertaDAO().execute(astaScelta.getId());

                List<Offerta> controfferte = new GetControfferteAttiveDAO()
                        .execute(astaScelta.getId(), migliorOfferta.getUtenteBase(), migliorOfferta.getImporto());

                if (controfferte.isEmpty()) {
                    break;
                }

                Offerta rilancio = controfferte.stream()
                        .max(Comparator.comparing(Offerta::getImportoControfferta))
                        .orElse(null);

                if (rilancio == null) {
                    break;
                }

                BigDecimal nuovoImporto = migliorOfferta.getImporto().add(incremento);

                if (nuovoImporto.compareTo(rilancio.getImportoControfferta()) > 0) {
                    break;
                }

                Offerta offertaRilancio = new Offerta();
                offertaRilancio.setAsta(astaScelta.getId());
                offertaRilancio.setUtenteBase(rilancio.getUtenteBase());
                offertaRilancio.setImporto(nuovoImporto);
                offertaRilancio.setAutomatica(true); //tutte le offerte generate dal sistema di rilanci sono automatiche
                offertaRilancio.setImportoControfferta(rilancio.getImportoControfferta());
                offertaRilancio.setData(LocalDate.now());
                offertaRilancio.setOra(LocalTime.now());

                String res = new AggiungiOffertaDAO().execute(offertaRilancio);
                //System.out.println("Rilancio automatico da " + rilancio.getUtenteBase() + ": " + res);

                rilanciEffettuati++;
                //Thread.sleep(50);
            }

            //System.out.println("Rilanci automatici finiti.");

        } catch (DAOException e) {
            System.err.println("Errore nei rilanci automatici: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

     */

    private void gestisciRilanciAutomatici(Asta astaScelta) {
        final BigDecimal incremento = BigDecimal.valueOf(0.50);
        final int MAX_RILANCI = 150; //per non caricare troppo il sistema
        final int DELAY_MS = 100; //delay tra ogni rilancio per evitare duplicazioni
        int rilanciEffettuati = 0;

        //set per tenere traccia degli utenti che hanno già rilanciato in questo ciclo
        Set<String> utentiRilanciati = new HashSet<>();

        try {
            while (rilanciEffettuati < MAX_RILANCI) {
                //delay per evitare conflitti di timestamp
                Thread.sleep(DELAY_MS);

                //offerta migliore attuale
                Offerta migliorOfferta = new GetMigliorOffertaDAO().execute(astaScelta.getId());

                //controfferte attive escludendo l'attuale miglior offerente
                List<Offerta> controfferte = new GetControfferteAttiveDAO()
                        .execute(astaScelta.getId(), migliorOfferta.getUtenteBase(), migliorOfferta.getImporto());

                if (controfferte.isEmpty()) {
                    //System.out.println("Nessuna controfferta attiva trovata. Fine rilanci.");
                    break;
                }

                //filtra gli utenti che hanno già rilanciato in questo ciclo
                controfferte = controfferte.stream()
                        .filter(c -> !utentiRilanciati.contains(c.getUtenteBase()))
                        .collect(Collectors.toList());

                if (controfferte.isEmpty()) {
                    //reset del set per il prossimo ciclo
                    utentiRilanciati.clear();
                    continue;
                }

                //trova la controfferta con l'importo massimo
                Offerta rilancio = controfferte.stream()
                        .max(Comparator.comparing(Offerta::getImportoControfferta))
                        .orElse(null);

                if (rilancio == null) {
                    break;
                }

                BigDecimal nuovoImporto = migliorOfferta.getImporto().add(incremento);

                //verifico se il nuovo importo supera il massimo della controfferta
                if (nuovoImporto.compareTo(rilancio.getImportoControfferta()) > 0) {
                    //System.out.println("Importo " + nuovoImporto + " supera il max controfferta di " + rilancio.getUtenteBase() + " (" + rilancio.getImportoControfferta() + ")");
                    break;
                }

                //creo l'offerta di rilancio
                Offerta offertaRilancio = new Offerta();
                offertaRilancio.setAsta(astaScelta.getId());
                offertaRilancio.setUtenteBase(rilancio.getUtenteBase());
                offertaRilancio.setImporto(nuovoImporto);
                offertaRilancio.setAutomatica(true);
                offertaRilancio.setImportoControfferta(rilancio.getImportoControfferta());
                offertaRilancio.setData(LocalDate.now());

                //imposto l'ora con alta precisione per evitare duplicati
                LocalTime oraConPrecisione = LocalTime.now();
                oraConPrecisione = oraConPrecisione.plusNanos(rilanciEffettuati * 100000L);
                offertaRilancio.setOra(oraConPrecisione);

                //System.out.println("Timestamp offerta: " + offertaRilancio.getData() + " " + oraConPrecisione.format(DateTimeFormatter.ofPattern("HH:mm:ss.SSSSSS")));

                try {
                    String res = new AggiungiOffertaDAO().execute(offertaRilancio);
                    //System.out.println("Rilancio automatico #" + (rilanciEffettuati + 1) + " da " + rilancio.getUtenteBase() + " con importo " + nuovoImporto + ": " + res);

                    //aggiungo l'utente al set dei rilanciati
                    utentiRilanciati.add(rilancio.getUtenteBase());

                } catch (DAOException e) {
                    if (e.getMessage().contains("Duplicate entry") || e.getMessage().contains("Errore 23000")) {
                        System.err.println("Offerta duplicata per " + rilancio.getUtenteBase() +
                                " al timestamp " + oraConPrecisione.format(DateTimeFormatter.ofPattern("HH:mm:ss.SSSSSS")) +
                                ". Salto questo rilancio.");

                        //aggiungo comunque il rilancio per evitare inconsistenze
                        utentiRilanciati.add(rilancio.getUtenteBase());
                        //attendo più tempo prima di rilanciare nuovamente
                        Thread.sleep(200);
                        continue;
                    } else if (e.getMessage().contains("Sei già il miglior offerente")) {
                        System.out.println(rilancio.getUtenteBase() +
                                " è già il miglior offerente, passo al prossimo.");
                        continue;
                    } else {
                        throw e;
                    }
                }

                rilanciEffettuati++;
            }

            if (rilanciEffettuati >= MAX_RILANCI) {
                System.out.println("Raggiunto il limite massimo di rilanci (" + MAX_RILANCI + ")");
            } else {
                //System.out.println("Rilanci automatici completati. Totale rilanci: " + rilanciEffettuati);
            }

        } catch (InterruptedException e) {
            System.err.println("Thread dei rilanci automatici interrotto");
            Thread.currentThread().interrupt();
        } catch (DAOException e) {
            System.err.println("Errore nei rilanci automatici: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private void mostraProfilo() {
        try {
            int choice = UserView.dettagliProfiloUtente();
            //ritorna al chiamante
            if (choice == 1) {//prima modifico l'indirizzo di consegna
                modificaIndirizzoConsegna();

                //mostro le informazioni del profilo aggiornate al nuovo indirizzo
                mostraProfilo();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void modificaIndirizzoConsegna() {
        UtenteBase utente = new UtenteBase();
        Integer result;


        try {
            //chiedo all'utente di inserire il nuovo indirizzo di consegna
            UserView.modificaIndirizzoForm(utente);

            result = new ModificaIndirizzoDAO().execute(utente);

            //aggiorno i dati della sessione utente loggato
            LoggedUser.setIndirizzo(utente.getIndirizzo());
            LoggedUser.setCitta(utente.getCitta());
            LoggedUser.setCap(utente.getCAP());

            if (result == 0) {
                throw new DAOException("Errore: indirizzo di consegna non modificato.");
            }
        } catch (DAOException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void logout() throws SQLException {
        //chiudo la precedente connessione
        ConnectionFactory.closeConnection();
        System.out.println("\nÈ stata effettuata la disconnessione dalla sessione attuale...");
    }
}
