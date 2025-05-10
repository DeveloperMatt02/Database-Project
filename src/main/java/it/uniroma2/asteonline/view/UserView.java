package it.uniroma2.asteonline.view;

import it.uniroma2.asteonline.model.domain.UtenteBase;
import it.uniroma2.asteonline.utils.LoggedUser;

import java.io.IOException;
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
}
