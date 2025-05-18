package it.uniroma2.asteonline.view;

import it.uniroma2.asteonline.controller.UserController;
import it.uniroma2.asteonline.model.domain.*;
import it.uniroma2.asteonline.utils.LoggedUser;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class UserView extends CLIView{
    public static int showMenu() throws IOException {
        CLIView.showHeader();
        printLine();

        System.out.println("1) Visualizza tutte le aste attive");
        System.out.println("2) Elenco articoli aggiudicati");
        System.out.println("3) Visualizza le aste in corso a cui stai partecipando");
        System.out.println("4) Visualizza profilo utente");
        System.out.println("5) Logout");
        return getAndValidateInput(5);
    }

    public static int dettagliProfiloUtente() throws IOException {
        crossSeparator();
        System.out.println("\nDettagli profilo utente");
        crossSeparator();
        printLine();

        System.out.println("Username: " + LoggedUser.getUsername());
        System.out.println("Ruolo: " + LoggedUser.getRole());
        System.out.println("Nome: " + LoggedUser.getNome());
        System.out.println("Cognome: " + LoggedUser.getCognome());
        System.out.println("Codice Fiscale: " + LoggedUser.getCF());
        System.out.println("Data di Nascita: " + LoggedUser.getDataNascita());
        System.out.println("Città di Nascita: " + LoggedUser.getCittaNascita());

        crossSeparator();
        System.out.println("\nIndirizzo di spedizione");
        crossSeparator();
        printLine();

        System.out.println("Indirizzo: " + LoggedUser.getIndirizzo());
        System.out.println("CAP: " + LoggedUser.getCap());
        System.out.println("Città: " + LoggedUser.getCitta());
        System.out.println("Numero Carta (hash di verifica): " + LoggedUser.getNumeroCarta());

        printLine();
        System.out.println("1) Modifica indirizzo di consegna");
        printBackOption(2);

        return getAndValidateInput(2);
    }

    public static void modificaIndirizzoForm(UtenteBase utente) throws IOException {
        crossSeparator();
        System.out.println("\nInserisci il nuovo indirizzo di consegna");
        crossSeparator();
        printLine();

        //nuovo indirizzo
        System.out.println("Indirizzo attuale: " + LoggedUser.getIndirizzo());
        String newAddress = getNotEmptyInput("Inserisci il nuovo indirizzo: ");
        utente.setIndirizzo(newAddress);
        printLine();

        //nuova città
        System.out.println("Città attuale: " + LoggedUser.getCitta());
        String newCity = getNotEmptyInput("Inserisci la nuova città: ");
        utente.setCitta(newCity);
        printLine();

        //nuovo cap
        System.out.println("CAP attuale: " + LoggedUser.getCap());
        String newCAP;
        while (true) {
            newCAP = getNotEmptyInput("Inserisci il nuovo CAP: ");
            if (((Pattern.compile("\\d{5}")).matcher(newCAP)).matches()) {
                break;
            }
            System.out.println("Errore: Il CAP deve essere formato da esattamente 5 cifre.");
        }
        utente.setCAP(newCAP);

        crossSeparator();
        System.out.println("\nNuovi dati aggiornati");
        crossSeparator();
        printLine();

    }

    public static int showArticoliAggiudicati(List<Asta> asteAggiudicate) throws IOException {
        crossSeparator();
        System.out.println("\nArticoli aggiudicati");
        crossSeparator();
        printLine();

        //stampo per ogni asta vinta la descrizione e l'ID
        for (int i = 0; i < asteAggiudicate.size(); i++) {
            Asta a = asteAggiudicate.get(i);
            System.out.println((i + 1) + ". " + a.getDescrizione() + " - Asta ID: " + a.getId());
        }

        printLine();
        System.out.println((asteAggiudicate.size() + 1) + ". Torna indietro");
        printLine();

        return getAndValidateInput(asteAggiudicate.size() + 1);
    }

    public static void showDettagliAstaVinta(Asta asta) {
        crossSeparator();
        System.out.println("Dettagli Asta #" + asta.getId());
        System.out.println("Descrizione: " + asta.getDescrizione());
        System.out.println("Dimensioni: " + asta.getDimensioni());
        System.out.println("Categoria: " + asta.getCategoria());
        System.out.println("Prezzo base: " + asta.getPrezzoBase());
        System.out.println("Condizioni articolo: " + asta.getCondizioniArticolo());
        System.out.println("Data inizio: " + asta.getData());
        System.out.println("Durata: " + asta.getDurata() + " giorni");
        System.out.println("Offerta massima: €" + asta.getOffertaMassima());

        crossSeparator();
        System.out.println("Premi INVIO per tornare all'elenco.");
        new Scanner(System.in).nextLine();
    }

    public static int showAstePartecipate(List<Asta> astePartecipate) throws IOException {
        crossSeparator();
        System.out.println("\nAste partecipate");
        crossSeparator();
        printLine();

        //stampo per ogni asta partecipata la descrizione e l'ID
        for (int i = 0; i < astePartecipate.size(); i++) {
            Asta a = astePartecipate.get(i);
            System.out.println((i + 1) + ". " + a.getDescrizione() + " - Asta ID: " + a.getId());
        }

        printLine();
        System.out.println((astePartecipate.size() + 1) + ". Torna indietro");
        printLine();

        return getAndValidateInput(astePartecipate.size() + 1);
    }


    public static int showDettagliAstaPartecipata(Asta asta) throws IOException {
        printGenericDetailsAsta(asta);

        crossSeparator();
        System.out.println("Dettagli della tua ultima offerta");
        crossSeparator();
        printLine();

        System.out.println("Importo: " + asta.getImportoOffertaUtente());
        System.out.println("Data: " + asta.getDataOffertaUtente());
        System.out.println("Ora: " + asta.getOraOffertaUtente());

        printLine();

        printRemainingTime(asta);

        printLine();
        System.out.println("1) Aggiungi offerta");
        printBackOption(2);

        return getAndValidateInput(2);
    }

    private static void printGenericDetailsAsta(Asta asta) {
        crossSeparator();
        System.out.println("Dettagli Asta #" + asta.getId());
        System.out.println("Descrizione: " + asta.getDescrizione());
        System.out.println("Dimensioni: " + asta.getDimensioni());
        System.out.println("Categoria: " + asta.getCategoria());
        System.out.println("Prezzo base: " + asta.getPrezzoBase());
        System.out.println("Condizioni articolo: " + asta.getCondizioniArticolo());
        System.out.println("Data inizio: " + asta.getData());
        System.out.println("Durata: " + asta.getDurata() + " giorni");
        System.out.println("Stato: " + asta.getStatoAsta());
        System.out.println("Offerta massima: €" + asta.getOffertaMassima());
        System.out.println("Utente Amministratore: " + asta.getUtenteAmministratore());
    }

    private static void printRemainingTime(Asta asta) {
        long tempoRimanente = asta.getTempoRimanenteSec();
        long ore = tempoRimanente / 3600;
        long minuti = (tempoRimanente % 3600) / 60;
        long secondi = tempoRimanente % 60;
        String tempoFormatted = String.format("%02d:%02d:%02d", ore, minuti, secondi);

        System.out.println("Tempo rimanente alla chiusura: " + tempoFormatted);
    }

    public static int showDettagliAsta(Asta asta) throws IOException {
        printGenericDetailsAsta(asta);

        printLine();

        printRemainingTime(asta);

        printLine();
        System.out.println("1) Aggiungi offerta");
        printBackOption(2);

        return getAndValidateInput(2);
    }


    public static void showAggiungiOffertaForm(Offerta offerta) {
        //TODO::
    }

    public static int showVisualizzaAsteAttiveMenu() throws IOException {
        crossSeparator();
        System.out.println("Visualizza aste attive");
        crossSeparator();

        printLine();

        System.out.println("1) Visualizza tutte le aste attive");
        System.out.println("2) Filtra aste attive per categoria");
        System.out.println("3) Filtra aste attive per amministratore");
        printBackOption(4);

        return getAndValidateInput(4);
    }

    public static Categoria showFiltraAsteCatMenu(Categoria catTree, UserController controller) throws IOException {
        crossSeparator();
        System.out.println("Filtra aste attive per categoria");
        crossSeparator();
        printLine();

        printCatTree(catTree, 0);

        while (true) {

            //inserisco il nome della categoria
            String chosenCat = getNotEmptyInput("Inserisci il nome della categoria: ");

            //cerco la categoria
            Categoria foundCat = controller.trovaCategoriaPerNome(catTree, chosenCat);

            if (foundCat == null) {
                System.out.println("Categoria non trovata. Inserire un nome valido.");
                continue; //provo a chiedere nuovamente
            }

            return foundCat;
        }
    }

    public static Utente showFiltraAsteAdminMenu(List<Utente> adminList) throws IOException {
        crossSeparator();
        System.out.println("Filtra aste attive per amministratore");
        crossSeparator();
        printLine();

        //stampo per ogni amministratore CF, Nome e Cognome.
        for (int i = 0; i < adminList.size(); i++) {
            Utente u = adminList.get(i);
            System.out.println((i + 1) + ". " + u.getNome() + " " + u.getCognome() + " - CF: " + u.getCodiceFiscale());
        }

        while (true) {
            //inserisci codice fiscale utente admin
            String chosenCf = getNotEmptyInput("Inserisci il codice fiscale: ");

            //cerco codice fiscale nella lista
            for (Utente admin : adminList) {
                if (admin.getCodiceFiscale().equalsIgnoreCase(chosenCf)) {
                    return admin;
                }
            }

            System.out.println("Codice fiscale non valido. Riprova.");
        }
    }


    public static int showAsteFiltrate(List<Asta> asteFiltrate) throws IOException {
        crossSeparator();
        System.out.println("\nAste filtrate");
        crossSeparator();
        printLine();

        //stampo per ogni asta partecipata la descrizione e l'ID
        for (int i = 0; i < asteFiltrate.size(); i++) {
            Asta a = asteFiltrate.get(i);
            System.out.println((i + 1) + ". " + a.getDescrizione() + " - Asta ID: " + a.getId());
        }

        printLine();
        System.out.println((asteFiltrate.size() + 1) + ". Torna indietro");
        printLine();

        return getAndValidateInput(asteFiltrate.size() + 1);
    }

    public static int showAste(List<Asta> asteAttive) throws IOException {
        crossSeparator();
        System.out.println("\nAste attive");
        crossSeparator();
        printLine();

        //stampo per ogni asta partecipata la descrizione e l'ID
        for (int i = 0; i < asteAttive.size(); i++) {
            Asta a = asteAttive.get(i);
            System.out.println((i + 1) + ". " + a.getDescrizione() + " - Asta ID: " + a.getId());
        }

        printLine();
        System.out.println((asteAttive.size() + 1) + ". Torna indietro");
        printLine();

        return getAndValidateInput(asteAttive.size() + 1);
    }



}
