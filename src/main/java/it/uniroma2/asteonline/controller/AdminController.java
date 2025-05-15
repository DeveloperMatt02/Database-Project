package it.uniroma2.asteonline.controller;

import it.uniroma2.asteonline.exception.DAOException;
import it.uniroma2.asteonline.factory.ConnectionFactory;
import it.uniroma2.asteonline.model.dao.*;
import it.uniroma2.asteonline.model.domain.Asta;
import it.uniroma2.asteonline.model.domain.Categoria;
import it.uniroma2.asteonline.model.domain.Offerta;
import it.uniroma2.asteonline.model.domain.Role;
import it.uniroma2.asteonline.utils.LoggedUser;
import it.uniroma2.asteonline.utils.StatoAsta;
import it.uniroma2.asteonline.view.AdminView;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminController implements Controller {
    private Categoria categoriesTree;

    @Override
    public void start() {
        //carico la lista delle categorie e creo l'albero
        loadCategoriesTree();

        //avvio la logica principale
        adminHomepage();
    }



    //TODO:: utente amministratore cosa deve fare???
    /*
        - inserire un nuovo articolo e inizializzare l'asta associata ad esso
        - visualizzare le aste ancora in corso e gestirne il ciclo di vita (inizializzazione, chiusura anticipata, modifica dei dati dell'asta)
        - gestione delle categorie
        - visualizzare lo storico delle aste create dall'utente amministratore con tutte le relative informazioni (incluse le offerte)
        - fare logout

        N.B. per ogni asta deve essere possibile visualizzare le offerte generate dal sistema di controfferta automatica
    */

    private void adminHomepage() {
        boolean running = true;
        while (running) {
            int choice;
            try {
                choice = AdminView.showMenu();
                switch (choice) {
                    case 1 -> creaAsta();
                    case 2 -> gestisciAste();
                    case 3 -> gestisciCategorie();
                    case 4 -> storicoAste();
                    case 5 -> mostraProfilo();
                    case 6 -> {
                        logout();
                        running = false;
                    }
                }
            } catch (IOException | SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void mostraProfilo() {
        try {
            AdminView.dettagliProfiloUtente();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void creaAsta() {
        //per creare una nuova asta devono prima essere aggiunte tutte le informazioni sull articolo
        Asta asta = new Asta();
        Categoria categoria = new Categoria();

        try {
            //aggiungo tutte le informazioni sull'asta
            AdminView.astaForm(asta);

            //aggiungo altre informazioni al model prima di procedere con l'inserimento nel db
            asta.setUtenteAmministratore(LoggedUser.getCF());

            //imposto lo stato dell'asta in base alla data
            setStatoAsta(asta);

            //aggiungo la categoria all'asta
            AdminView.selectCategoryForm(categoria);

            //Eseguo la procedura per memorizzare nel database la nuova asta con la categoria associata
            System.out.print("\n++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n");
            System.out.println(new CreaAstaDAO().execute(asta, categoria));
            System.out.print("\n++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n");

        } catch (IOException | DAOException e) {
            e.printStackTrace();
        }

    }

    private void setStatoAsta(Asta asta) {
        LocalDateTime now = LocalDateTime.now();

        if (asta.getData().isAfter(now)) {
            asta.setStatoAsta(StatoAsta.FUTURA.name());
        } else {
            asta.setStatoAsta(StatoAsta.ATTIVA.name());
        }
    }

    private void gestisciAste() {
        boolean running = true;
        while (running) {
            int choice;
            try {
                choice = AdminView.showGestisciAsteMenu();
                switch (choice) {
                    case 1 -> visualizzaAsteAttive();
                    case 2 -> visualizzaAsteProgrammate();
                    case 3 -> {
                        //esce dal ciclo e torno al chiamante
                        running = false;
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void visualizzaAsteProgrammate() {
        //TODO::
    }

    private void visualizzaAsteAttive() {
        //TODO::
    }

    private void gestisciCategorie() {
        boolean running = true;
        while (running) {
            int choice;
            try {
                choice = AdminView.showGestisciCategorieMenu();
                switch (choice) {
                    case 1 -> aggiungiCat();
                    case 2 -> modificaCat();
                    case 3 -> rimuoviCat();
                    case 4 -> visualizzaListaCat();
                    case 5 -> {
                        //esce dal ciclo e torno al chiamante
                        running = false;
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void visualizzaListaCat() {
        try {
            //stampo l'albero delle categorie
            if(AdminView.showListaCategorie(this.categoriesTree) == 1){
                System.out.println("Torno indietro...");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void loadCategoriesTree() {
        try {
            //carico la lista delle categorie
            List<Categoria> fullCatList = new ListaCategorieDAO().execute();

            //creo l'albero delle categorie
            this.categoriesTree = getCategoriesTree(fullCatList);
        } catch (DAOException e) {
            throw new RuntimeException(e);
        }
    }

    private void rimuoviCat() {

        //TODO::
        /* se una categoria viene rimossa ed ha figli, tutti i figli vengono rimossi allo stesso modo.
           se una categoria rimossa (ed eventualmente i suoi figli) ha delle aste associate, esse vengono riassegnate ad una categoria predefinita di default
         */

        Categoria catDel;

        try {
            catDel = AdminView.eliminaCatForm(categoriesTree, this);

            if(modifiedCat != null) {
                //provo a modificare la categoria nel db

                //Eseguo la procedura per memorizzare nel database la nuova categoria
                System.out.print("\n++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n");
                System.out.println(new EliminaCategoriaDAO().execute(catDel));
                System.out.print("\n++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n");

                //aggiorno l'albero delle categorie
                loadCategoriesTree();
            }
        } catch (DAOException | IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void modificaCat() {
        Categoria modifiedCat;
        Categoria oldCat = new Categoria();

        try {
            modifiedCat = AdminView.modificaCatForm(oldCat, categoriesTree, this);

            if(modifiedCat != null) {
                //provo a modificare la categoria nel db

                //Eseguo la procedura per memorizzare nel database la nuova categoria
                System.out.print("\n++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n");
                System.out.println(new ModificaCategoriaDAO().execute(oldCat, modifiedCat));
                System.out.print("\n++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n");

                //aggiorno l'albero delle categorie
                loadCategoriesTree();
            }
        } catch (DAOException | IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void aggiungiCat() {
        Categoria categoria = new Categoria();

        try {
            //avvio il form di aggiunta di una nuova categoria
            AdminView.aggiungiCatForm(categoria, categoriesTree, this);

            //Eseguo la procedura per memorizzare nel database la nuova categoria
            System.out.print("\n++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n");
            System.out.println(new AggiungiCategoriaDAO().execute(categoria));
            System.out.print("\n++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n");

            //aggiorno l'albero delle categorie
            loadCategoriesTree();

        } catch (DAOException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void storicoAste() {
        List<Asta> asteTerminate;

        try {
            //ottengo tutte le aste terminate
            asteTerminate = new VisualizzaAsteStatoDAO().execute(LoggedUser.getCF(), StatoAsta.TERMINATA);

            //mostro l'elenco delle aste
            while (true) {
                int choice;
                try {
                    choice = AdminView.showStoricoAste(asteTerminate);

                    if (asteTerminate.isEmpty() || choice > asteTerminate.size()) {
                        //nessuna asta presente nello storico oppure scelto torna indietro
                        break;
                    } else {
                        Asta selezionata = asteTerminate.get(choice - 1);

                        //apro i dettagli dell'asta
                        dettagliAsta(selezionata);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }


        } catch (DAOException e) {
            throw new RuntimeException(e);
        }

    }

    private void dettagliAsta(Asta selezionata) {
        int choice;
        int maxChoice = switch (selezionata.getStatoAsta()) {
            case "ATTIVA" -> 4;    // 3 azioni + 1 "indietro"
            case "FUTURA", "TERMINATA" -> 2;    // 1 azione + 1 "indietro"
            default -> 1;
        };

        try {
            while (true) {
                //mostro i dettagli dell'asta selezionata
                choice = AdminView.showDettagliAsta(selezionata);

                //per uscire dal ciclo while
                if (choice == maxChoice || choice == -1) {
                    break;
                }

                //in base allo stato dell'asta mostro le varie opzioni disponibili
                switch (selezionata.getStatoAsta()) {
                    case "TERMINATA" -> {
                        if (choice == 1) {
                            offertePerAsta(selezionata.getId());
                        }
                    }
                    case "FUTURA" -> {
                        if (choice == 1) {
                            modificaAsta(selezionata.getId());
                        }
                    }
                    case "ATTIVA" -> {
                        switch (choice) {
                            case 1 -> modificaAsta(selezionata.getId());
                            case 2 -> offertePerAsta(selezionata.getId());
                            case 3 -> chiudiAsta(selezionata.getId());
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void chiudiAsta(int idAsta) {
        //TODO::
    }

    private void modificaAsta(int idAsta) {
        //TODO::
    }

    private void offertePerAsta(int idAsta) {
        List<Offerta> listaOff;

        try {
            //recupero dal db le offerte di un'asta
            listaOff = new VisualizzaOfferteAstaDAO().execute(idAsta);

            AdminView.mostraOffertePerAsta(listaOff);
        } catch (DAOException e) {
            throw new RuntimeException(e);
        }

    }

    private void logout() throws SQLException {
        //chiudo la precedente connessione
        ConnectionFactory.closeConnection();
        System.out.println("\nÈ stata effettuata la disconnessione dalla sessione attuale...");
    }

    private Categoria getCategoriesTree(List<Categoria> fullCategoriesList) {
        //creo una hashmap che associa al nome della categoria il suo model (contenente tutte le informazioni necessarie a ricostruire l'albero)
        Map<String, Categoria> catMap = new HashMap<>();

        //aggiungo un nodo padre di livello 0 (che farà da radice generale di tutto l'albero delle categorie)
        Categoria root = new Categoria("Radice", 0, null);

        //inserisco tutte le categorie ottenute dal db nella mappa
        for (Categoria c : fullCategoriesList) {
            catMap.put(c.getNomeCategoria(), c);
        }

        //costruisco le relazioni padre-figli a partire dalle informazioni contenute in ciascun model
        for (Categoria c : fullCategoriesList) {
            if (c.getCategoriaSuperiore() == null) {
                root.addFiglio(c); //sono le categorie di livello 1
            } else {
                //data una categoria prendo il suo padre, se presente, e aggiungo a esso questa categoria come figlia
                Categoria padre = catMap.get(c.getCategoriaSuperiore());
                if(padre != null) {
                    padre.addFiglio(c);
                }
            }
        }

        return root; //restituisco l'albero delle categorie
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
}
