package it.uniroma2.asteonline.view;

import it.uniroma2.asteonline.controller.AdminController;
import it.uniroma2.asteonline.model.domain.Asta;
import it.uniroma2.asteonline.model.domain.Categoria;
import it.uniroma2.asteonline.model.domain.Offerta;
import it.uniroma2.asteonline.utils.LoggedUser;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdminView extends CLIView {
    public static int showMenu() throws IOException {
        CLIView.showHeader();

        System.out.println("1. Inserisci nuovo articolo e inizializza asta");
        System.out.println("2. Gestisci aste");
        System.out.println("3. Gestisci categorie");
        System.out.println("4. Storico delle aste");
        System.out.println("5. Visualizza profilo utente");
        System.out.println("6. Logout");

        return getAndValidateInput(6);
    }

    public static int showGestisciAsteMenu() throws IOException {
        printLine();
        System.out.println("Le aste che sono terminate sono disponibili nella sezione \"Storico delle aste\".");

        System.out.println("1. Aste in corso");
        System.out.println("2. Aste programmate");
        printBackOption(3);

        return getAndValidateInput(3);
    }

    public static void astaForm(Asta asta) throws IOException {
        crossSeparator();
        System.out.println("\nInserisci dati per la creazione di una nuova asta");
        crossSeparator();

        //dimensioni
        String dimensioni;
        while (true) {
            try {
                dimensioni = getNotEmptyInput("Inserisci le dimensioni dell'oggetto (es. 100x30x40): ");

                //divido l'input in parti (separandole dalla x)
                String[] parti = dimensioni.trim().toLowerCase().split("x");

                if (parti.length != 3) {
                    System.out.println("Errore: il formato deve essere esattamente 'LunghezzaxLarghezzaxAltezza' con tre numeri.");
                    continue;
                }

                try {
                    int lunghezza = Integer.parseInt(parti[0].trim());
                    int larghezza = Integer.parseInt(parti[1].trim());
                    int altezza = Integer.parseInt(parti[2].trim());

                    if (lunghezza <= 0 || larghezza <= 0 || altezza <= 0) {
                        System.out.println("Errore: le dimensioni devono essere numeri interi positivi.");
                        continue;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Errore: inserire solo numeri interi (es. 30x20x15).");
                }

                break;
            } catch (IOException e) {
                System.out.println("Errore: Inserimento delle dimensioni non completato.");
            }
        }
        asta.setDimensioni(dimensioni);


        // Data + ora
        LocalDateTime dateTime;
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        while (true) {
            try {
                String dateInput = getNotEmptyInput("Inserisci la data (gg/MM/yyyy): ");
                String oraInput = getNotEmptyInput("Ora di inizio (0-23): ");
                String minutiInput = getNotEmptyInput("Minuti di inizio (0-59): ");

                int ora = Integer.parseInt(oraInput.trim());
                int minuto = Integer.parseInt(minutiInput.trim());

                //effettuo la verifica su ora e minuti
                if (ora < 0 || ora > 23 || minuto < 0 || minuto > 59) {
                    System.out.println("Errore: ora o minuti fuori intervallo.");
                    continue;
                }

                LocalDate date = LocalDate.parse(dateInput, dateFormatter);
                LocalTime time = LocalTime.of(ora, minuto);

                dateTime = LocalDateTime.of(date, time);

                if (dateTime.isBefore(LocalDateTime.now())) {
                    System.out.println("Errore: la data scelta è antecedente alla data odierna.");
                    continue;
                }
                break;
            } catch (Exception e) {
                System.out.println("Errore: Assicurati del formato corretto." + e.getMessage());
            }
        }
        asta.setData(dateTime);

        //durata
        String durata;
        while (true) {
            durata = getNotEmptyInput("Inserisci la durata dell'asta in giorni (min 1, max 7): ");

            try {
                int durataValue = Integer.parseInt(durata);
                asta.setDurata(durataValue);
                break;
            } catch (NumberFormatException e) {
                System.out.println("Errore: Valore non valido. Riprova nuovamente!");
            }
        }

        //descrizione
        String descrizione = getNotEmptyInput("Inserisci una descrizione del prodotto da mettere all'asta: ");
        asta.setDescrizione(descrizione);

        //prezzo base
        BigDecimal prezzo = null;
        while (true) {
            String prezzoInput = getNotEmptyInput("Inserisci il prezzo base (es. 99.99): ");
            try {
                prezzo = new BigDecimal(prezzoInput);
                if (prezzo.precision() > 7) {
                    System.out.println("Errore: il prezzo deve avere al massimo 7 cifre totali.");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("Errore: formato del prezzo non valido.");
            }
        }
        asta.setPrezzoBase(prezzo);

        //condizioni articolo
        System.out.println("Inserisci le condizioni dell'articolo da mettere all'asta.");
        ArrayList<String> condizioniAmmesse = new ArrayList<>(Arrays.asList(
                "Nuovo",
                "Come nuovo",
                "Ottime condizioni",
                "Buone condizioni",
                "Accettabili",
                "Da riparare"
        ));

        while (true) {
            System.out.println("Scegli le condizioni dell'articolo tra le seguenti: ");
            for (int i = 0; i < condizioniAmmesse.size(); i++) {
                System.out.printf("  %d. %s%n", i + 1, condizioniAmmesse.get(i));
            }

            String scelta = getNotEmptyInput("Inserisci il numero corrispondente: ");
            try {
                int sceltaIndex = Integer.parseInt(scelta.trim()) - 1;

                if (sceltaIndex >= 0 && sceltaIndex < condizioniAmmesse.size()) {
                    asta.setCondizioniArticolo(condizioniAmmesse.get(sceltaIndex));
                    break;
                } else {
                    System.out.println("Errore: Scelta non valida. Riprova.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Errore: inserisci un numero valido.");
            }
        }
    }

    public static Categoria selectCategoryForm(Categoria categoriesTree, AdminController controller) throws IOException {
        //stampo tutte le categorie esistenti
        System.out.println("\nCategorie esistenti:");
        printCatTree(categoriesTree, 0);

        printLine();

        while (true) {
            //inserisco il nome della categoria scelta
            String selCat = getNotEmptyInput("Inserisci il nome della categoria: ");

            //cerco la categoria
            Categoria foundCat = controller.trovaCategoriaPerNome(categoriesTree, selCat);

            if (foundCat == null) {
                System.out.println("Categoria non trovata. Inserire un nome valido.");
                continue; //provo a chiedere nuovamente
            }

            return foundCat;
        }
    }

    public static boolean confermaInizializzazioneAsta(Asta asta, Categoria categoria) throws IOException {
        LocalDateTime dataInizio = asta.getData();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        crossSeparator();
        System.out.println("\nStai per inizializzare una nuova asta con i seguenti dati:");
        crossSeparator();
        printLine();

        System.out.println("Descrizione: " + asta.getDescrizione());
        System.out.println("Dimensioni: " + asta.getDimensioni());
        System.out.println("Prezzo base: €" + asta.getPrezzoBase());
        System.out.println("Durata: " + asta.getDurata() + " giorni");
        System.out.println("Data inizio: " + dataInizio.format(formatter));
        System.out.println("Condizioni articolo: " + asta.getCondizioniArticolo());
        System.out.println("Categoria: " + categoria.getNomeCategoria());

        printLine();
        System.out.println("1. Sì, inizializza l'asta");
        printBackOption(2);

        int scelta = getAndValidateInput(2);
        return scelta == 1;
    }


    public static void dettagliProfiloUtente() throws IOException {
        crossSeparator();
        System.out.println("\nDettagli profilo utente");
        crossSeparator();
        printLine();

        System.out.println("Username: " + LoggedUser.getUsername());
        System.out.println("Ruolo: " + LoggedUser.getRole());
        System.out.println("Nome: " + LoggedUser.getNome());
        System.out.println("Cognome: " + LoggedUser.getCognome());
        System.out.println("Codice Fiscale: " + LoggedUser.getCF());

        printLine();
        printBackOption(1);
        getAndValidateInput(1);
    }

    public static int showGestisciCategorieMenu() throws IOException {
        printLine();

        System.out.println("1. Aggiungi una nuova categoria");
        System.out.println("2. Modifica categoria esistente");
        System.out.println("3. Rimuovi categoria");
        System.out.println("4. Visualizza lista categorie");
        printBackOption(5);

        return getAndValidateInput(5);
    }

    public static String eliminaCatForm(Categoria categoriesTree, AdminController controller) throws IOException {
        crossSeparator();
        System.out.println("\nElimina categoria esistente");
        crossSeparator();
        printLine();

        System.out.println("N.B. Eliminare una categoria con aste assegnate causerà la riassegnazione delle aste sulla categoria \"Default\".");
        System.out.println("Inoltre, la cancellazione di una categoria che ha dei figli causerà la cancellazione ricorsiva di tutti i suoi figli.");

        printLine();

        if (categoriesTree.getFigli().isEmpty()) {
            System.out.println("Non ci sono categorie esistenti da eliminare");
            return null;
        }

        //stampo tutte le categorie esistenti
        System.out.println("\nCategorie esistenti:");
        printCatTree(categoriesTree, 0);

        printLine();

        while (true) {
            //inserisco il nome della categoria da modificare
            String delCat = getNotEmptyInput("Inserisci il nome della categoria da eliminare: ");

            //cerco la categoria
            Categoria foundDelCat = controller.trovaCategoriaPerNome(categoriesTree, delCat);

            if (foundDelCat == null) {
                System.out.println("Categoria da eliminare non trovata. Inserire un nome valido.");
                continue; //provo a chiedere nuovamente
            } else if (foundDelCat.getNomeCategoria().equals("Default")) {
                System.out.println("Non puoi eliminare la categoria di default.");
                continue;
            }

            return foundDelCat.getNomeCategoria();
        }

    }

    public static Categoria modificaCatForm(Categoria oldCat, Categoria categoriesTree, AdminController controller) throws IOException {
        crossSeparator();
        System.out.println("\nModifica categoria esistente");
        crossSeparator();
        printLine();

        if (categoriesTree.getFigli().isEmpty()) {
            System.out.println("Non ci sono categorie esistenti da modificare");
            return null;
        }

        //stampo tutte le categorie esistenti
        System.out.println("\nCategorie esistenti:");
        printCatTree(categoriesTree, 0);

        printLine();

        while (true) {
            //inserisco il nome della categoria da modificare
            String cat = getNotEmptyInput("Inserisci il nome della categoria da modificare: ");

            //cerco la categoria
            Categoria foundCat = controller.trovaCategoriaPerNome(categoriesTree, cat);

            if (foundCat == null) {
                System.out.println("Categoria da modificare non trovata. Inserire un nome valido.");
                continue; //provo a chiedere nuovamente
            } else if (foundCat.getNomeCategoria().equals("Default")) {
                System.out.println("Non puoi modificare la categoria di default.");
                continue;
            }

            //assegno a oldCat il nome della categoria da modificare
            oldCat.setNomeCategoria(foundCat.getNomeCategoria());

            //chiedo il nuovo nome da assegnare alla categoria
            foundCat.setNomeCategoria(getNotEmptyInput("Nuovo nome da assegnare alla categoria scelta: "));

            return foundCat;
        }
    }

    public static void aggiungiCatForm(Categoria categoria, Categoria categoriesTree, AdminController controller) throws IOException {
        crossSeparator();
        System.out.println("\nCreazione nuova categoria");
        crossSeparator();
        printLine();

        //stampo tutte le categorie esistenti
        System.out.println("\nCategorie esistenti:");
        printCatTree(categoriesTree, 0);
        if (categoriesTree.getFigli().isEmpty()) {
            System.out.println("Verrà creata una categoria di livello 1.");
            categoria.setLivello(1);
            categoria.setCategoriaSuperiore(null);
        }

        printLine();

        //inserisco il nome della nuova categoria
        String nome = getNotEmptyInput("Inserisci il nome della nuova categoria: ");
        categoria.setNomeCategoria(nome);

        //inserisco il nome della categoria padre (da lasciare vuoto se vogliamo aggiungere una categoria di livello 1)
        while (true) {
            String padre = getNotEmptyInput("\nInserisci il nome della categoria padre (lascia vuoto per categoria di livello 1): ");

            if (padre.isBlank()) {
                categoria.setLivello(1);
                categoria.setCategoriaSuperiore(null);
                break;

            } else {
                Categoria padreScelto = controller.trovaCategoriaPerNome(categoriesTree, padre);

                if (padreScelto == null) {
                    System.out.println("Categoria padre non trovata. Inserire un nome valido.");
                    categoria.setCategoriaSuperiore(null);
                    continue; //provo a chiedere nuovamente il nome del padre
                }

                int nuovoLivello = padreScelto.getLivello() + 1;

                categoria.setLivello(nuovoLivello);
                categoria.setCategoriaSuperiore(padreScelto.getNomeCategoria());

                return;
            }
        }
    }

    public static int showListaCategorie(Categoria catTree) throws IOException {
        printCatTree(catTree, 0); //utilizzo indent 0 perché voglio stampare dal nodo radice

        printLine();
        printBackOption(1);

        return getAndValidateInput(1);
    }

    public static int showStoricoAste(List<Asta> asteTerminate) throws IOException {
        crossSeparator();
        System.out.println("\nStorico aste");
        crossSeparator();
        printLine();

        if (asteTerminate.isEmpty()) {
            System.out.println("\nNessuna asta terminata trovata.");
            printLine();
            printBackOption(1);

            return getAndValidateInput(1);
        }

        printLine();
        System.out.println("Scegli un'asta da visualizzare:");
        printLine();

        for (int i = 0; i < asteTerminate.size(); i++) {
            Asta a = asteTerminate.get(i);
            System.out.printf("%d. Asta #%d - %s - %s\n", i + 1, a.getId(), a.getCategoria(), a.getDescrizione());
        }
        System.out.printf("\n%d. Torna indietro\n", asteTerminate.size() + 1);

        return getAndValidateInput(asteTerminate.size() + 1);
    }


    public static int showDettagliAsta(Asta asta) throws IOException {
        LocalDateTime dataInizio = asta.getData();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        crossSeparator();
        System.out.println("\nDettagli asta #" + asta.getId());
        crossSeparator();
        printLine();

        System.out.println("Categoria: " + asta.getCategoria());
        System.out.println("Descrizione: " + asta.getDescrizione());
        System.out.println("Data: " + dataInizio.format(formatter));
        System.out.println("Durata: " + asta.getDurata() + (asta.getDurata() == 1 ? " giorno" : " giorni"));
        System.out.println("Prezzo base: €" + asta.getPrezzoBase());

        if(asta.getStatoAsta().equals("ATTIVA") || asta.getStatoAsta().equals("TERMINATA")) {
            System.out.println("Offerta massima: €" + asta.getOffertaMassima());
            System.out.println("Numero offerte: " + asta.getNumOfferte());
        }

        if (asta.getStatoAsta().equals("TERMINATA") && asta.getUtenteBase() != null) {
            System.out.println("Vincitore: " + asta.getUtenteBase());
        } else if (asta.getStatoAsta().equals("ATTIVA") && asta.getUtenteBase() != null) {
            System.out.println("Miglior offerente attuale: " + asta.getUtenteBase());
        } else if (asta.getStatoAsta().equals("TERMINATA") && asta.getUtenteBase() == null) {
            System.out.println("Asta terminata senza vincitore.");
        }

        printLine();

        int choice;
        switch (asta.getStatoAsta()) {
            case "TERMINATA" -> {
                System.out.println("1. Visualizza offerte");
                printBackOption(2);
                choice = getAndValidateInput(2);
            }
            case "FUTURA" -> {
                //System.out.println("1. Modifica asta");
                printBackOption(1);
                choice = getAndValidateInput(1);
            }
            case "ATTIVA" -> {
                //System.out.println("1. Modifica asta");
                System.out.println("1. Visualizza offerte");
                System.out.println("2. Chiudi asta");
                printBackOption(3);
                choice = getAndValidateInput(3);
            }
            default -> {
                //non deve verificarsi mai
                System.out.println("Stato non riconosciuto.");
                return -1;
            }
        }
        return choice;
    }

    public static int mostraOffertePerAsta(List<Offerta> offerte) throws IOException {
        crossSeparator();
        System.out.println("\nOfferte per asta selezionata");
        crossSeparator();
        printLine();

        if (offerte.isEmpty()) {
            System.out.println("Nessuna offerta disponibile per questa asta.");
        } else {
            for (Offerta o : offerte) {
                System.out.printf("Utente: %s | Data: %s %s | Importo: %.2f | Automatica: %s\n",
                        o.getUtenteBase(),
                        o.getData(),
                        o.getOra(),
                        o.getImporto(),
                        o.isAutomatica() ? "Sì" : "No");
            }
        }

        printLine();
        printBackOption(1);

        return getAndValidateInput(1);
    }

    public static boolean chiediFiltroOfferteAutomatiche() throws IOException {
        System.out.println("Vuoi visualizzare solo le offerte generate dal sistema di controfferta automatica?");
        System.out.println("1. Sì");
        System.out.println("2. No");
        int scelta = getAndValidateInput(2);
        return scelta == 1;
    }


    public static int showAsteGeneriche(List<Asta> aste, String titolo) throws IOException {
        crossSeparator();
        System.out.println("\n" + titolo);
        crossSeparator();
        printLine();

        if (aste.isEmpty()) {
            System.out.println("Nessuna asta disponibile.");
            return 0;
        }

        int index = 1;
        for (Asta a : aste) {
            if (titolo.equals("Aste programmate")) {
                System.out.printf("%d. %s | %s | Prezzo base: €%.2f | Stato: %s\n",
                        index++, a.getCategoria(), a.getDescrizione(), a.getPrezzoBase(), a.getStatoAsta());
            } else {
                System.out.printf("%d. %s | %s | Prezzo base: €%.2f | Offerta max: %.2f | Stato: %s\n",
                        index++, a.getCategoria(), a.getDescrizione(), a.getPrezzoBase(), a.getOffertaMassima(), a.getStatoAsta());
            }

        }

        printLine();
        printBackOption(index);

        return getAndValidateInput(index);

    }

    public static boolean confermaChiusuraAsta(Asta asta) throws IOException {
        LocalDateTime dataInizio = asta.getData();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        crossSeparator();
        System.out.println("\nStai per chiudere un'asta con i seguenti dati:");
        crossSeparator();
        printLine();

        System.out.println("Descrizione: " + asta.getDescrizione());
        System.out.println("Dimensioni: " + asta.getDimensioni());
        System.out.println("Prezzo base: €" + asta.getPrezzoBase());
        System.out.println("Durata: " + asta.getDurata() + " giorni");
        System.out.println("Data inizio: " + dataInizio.format(formatter));
        System.out.println("Condizioni articolo: " + asta.getCondizioniArticolo());
        System.out.println("Categoria: " + asta.getCategoria());

        printLine();
        System.out.println("1. Sì, termina l'asta prima della scadenza impostata");
        printBackOption(2);

        int scelta = getAndValidateInput(2);
        return scelta == 1;
    }
}
