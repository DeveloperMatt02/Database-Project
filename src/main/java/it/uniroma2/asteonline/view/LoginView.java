package it.uniroma2.asteonline.view;

import it.uniroma2.asteonline.model.domain.CartaDiCredito;
import it.uniroma2.asteonline.model.domain.Credentials;
import it.uniroma2.asteonline.model.domain.UtenteBase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

public class LoginView extends CLIView {

    public static int chooseLoginOption() throws IOException {
        System.out.println("1. Login");
        System.out.println("2. Registrazione");
        System.out.println("3. Esci");
        return getAndValidateInput(3);
    }

    public static Credentials authenticate() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Username: ");
        String username = reader.readLine();
        System.out.print("Password: ");
        String password = reader.readLine();

        return new Credentials(username, password, null, null);
    }

    public static UtenteBase addNewUserForm() throws IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        UtenteBase newUser = new UtenteBase();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        crossSeparator();
        System.out.println("\nInserisci dati per l'aggiunta di un nuovo utente");
        crossSeparator();

        //Username
        String username = getNotEmptyInput("Username (minimo 4 caratteri): ");
        newUser.setUsername(username);

        //Password
        String password = getNotEmptyInput("Password (minimo 8 caratteri): ");
        newUser.setPassword(password);

        //Codice Fiscale
        while (true) {
            String cf = getNotEmptyInput("Codice fiscale: ");
            if (((Pattern.compile("^[A-Z0-9]{16}$")).matcher(cf)).matches()) {
                newUser.setCodiceFiscale(cf);
                break;
            }
            System.out.println("Errore: codice fiscale inserito non valido. Deve contenere esattamente 16 caratteri alfanumerici (capital case).");
        }

        //Nome
        String nome = getNotEmptyInput("Nome: ");
        newUser.setNome(nome);

        //Cognome
        String cognome = getNotEmptyInput("Cognome: ");
        newUser.setCognome(cognome);

        //DataNascita
        int parsed = 0;
        do {
            try {
                System.out.print("Data di nascita (formato gg/MM/aaaa): ");
                String dataString = reader.readLine();
                LocalDate dataNascita = LocalDate.parse(dataString, formatter);
                if (dataNascita.isBefore(LocalDate.now())) {
                    newUser.setDataNascita(dataNascita);
                    parsed = 1;
                } else {
                    System.out.println("Errore: La data di nascita inserita è successiva alla data odierna.");
                }
            } catch (DateTimeParseException e) {
                System.out.println("Errore: Formato data errato, riprovare.");
            }
        } while (parsed != 1);

        //CittàNascita
        String cittaNascita = getNotEmptyInput("Città nascita: ");
        newUser.setCittaNascita(cittaNascita);

        //Indirizzo, CAP, Città
        addAddressInfoForm(newUser);

        return newUser;
    }

    public static void addAddressInfoForm(UtenteBase utente) throws IOException {
        int done = 0;
        String indirizzo = null;
        String cap = null;
        String citta = null;

        while (done == 0) {
            //inserisco indirizzo
            indirizzo = getNotEmptyInput("Indirizzo (Via, Viale, Piazza, ...): ");

            //inserisco il civico
            citta = getNotEmptyInput("Città: ");

            //Controllo che il CAP sia formato da esattamente 5 cifre tramite regex
            while (true) {
                cap = getNotEmptyInput("CAP: ");
                if (((Pattern.compile("\\d{5}")).matcher(cap)).matches()) {
                    break;
                }
                System.out.println("Errore: Il CAP deve essere formato da esattamente 5 cifre.");
            }


            done = 1;
        }

        utente.setIndirizzo(indirizzo);
        utente.setCAP(cap);
        utente.setCitta(citta);

        crossSeparator();
        System.out.println("\nIndirizzo aggiunto con successo!");
        crossSeparator();
    }


    public static CartaDiCredito addCreditCardInfoForm() throws IOException {
        CartaDiCredito cc = new CartaDiCredito();

        // Acquisisco numero carta
        String numeroCarta = getNotEmptyInput("Inserisci il numero della carta di credito: ");

        // Validazione del numero carta
        while (numeroCarta.trim().isEmpty() || !numeroCarta.matches("\\d{16}")) {
            System.out.println("Errore: Formato non valido. Inserisci un numero di 16 cifre.");
            numeroCarta = getNotEmptyInput("Inserisci il numero della carta di credito: ");
        }

        // Acquisisco CVV (suppongo che ogni CVV abbia lunghezza pari a 3 cifre numeriche)
        String CVV;
        int valid = 0;

        do {
            try {
                CVV = getNotEmptyInput("Inserisci il CVV (3 cifre): ");

                // Validazione del CVV
                if (CVV.length() != 3 || !CVV.matches("\\d{3}")) {
                    System.out.println("Errore: Formato CVV non valido. Inserisci un CVV valido che abbia esclusivamente 3 cifre numeriche.");
                } else {
                    valid = 1; //CVV valido
                }
            } catch (IOException e) {
                System.out.println("Errore: Inserimento CVV non completato correttamente. Utilizzo valore predefinito.");
                CVV = "000"; // Valore predefinito in caso di errore
            }
        } while (valid != 1);


        // Acquisisco data di scadenza
        YearMonth dataScadenza = null;
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/yyyy");
        valid = 0;

        do {
            String dataInput = getNotEmptyInput("Inserisci la data di scadenza (MM/aaaa): ");

            try {
                dataScadenza = YearMonth.parse(dataInput, dateFormatter);

                // Verifica che la data non sia passata
                YearMonth now = YearMonth.now();
                if (dataScadenza.isBefore(now)) {
                    System.out.println("Errore: la carta risulta scaduta. Inserisci una carta valida.");
                } else {
                    valid = 1;
                }
            } catch (DateTimeParseException e) {
                System.out.println("Errore: Formato data non valido. Operazione annullata.");
                System.out.println(e.getMessage());
            }
        } while (valid != 1);


        // Imposto i valori nella carta di credito
        cc.setNumeroCarta(numeroCarta);
        cc.setCVV(CVV);
        cc.setDataScadenza(dataScadenza.atEndOfMonth()); //imposto la data di scadenza all'ultimo giorno del mese in modo da salvarla con il tipo corretto.

        crossSeparator();
        System.out.println("\nCarta di credito aggiunta con successo!");
        crossSeparator();

        return cc;
    }
}
