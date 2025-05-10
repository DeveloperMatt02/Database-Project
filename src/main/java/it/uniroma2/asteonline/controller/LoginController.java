package it.uniroma2.asteonline.controller;

import it.uniroma2.asteonline.exception.DAOException;
import it.uniroma2.asteonline.model.dao.GetUserDetailsDAO;
import it.uniroma2.asteonline.model.dao.LoginProcedureDAO;
import it.uniroma2.asteonline.model.dao.RegistraUtenteDAO;
import it.uniroma2.asteonline.model.domain.CartaDiCredito;
import it.uniroma2.asteonline.model.domain.Credentials;
import it.uniroma2.asteonline.model.domain.Utente;
import it.uniroma2.asteonline.model.domain.UtenteBase;
import it.uniroma2.asteonline.view.LoginView;

import java.io.IOException;

public class LoginController implements Controller{
    Credentials credentials = null;

    public Credentials getCred() {
        return credentials;
    }

    public Utente getLoggedUser() {
        Utente utente;

        try {
            //ottengo prima le informazioni sull'utente che si è appena loggato
            utente = new GetUserDetailsDAO().execute(credentials.getCF(), credentials.getRole());

            //aggiungo altre informazioni già conosciute
            utente.setUsername(credentials.getUsername());
            utente.setPassword(credentials.getPassword());

        } catch (DAOException e) {
            throw new RuntimeException(e);
        }

        return utente;
    }

    @Override
    public void start() {
        try {
            int choice = LoginView.chooseLoginOption();
            switch (choice) {
                case 1 -> {
                    login();
                }
                case 2 -> {
                    registration();
                    start();
                }
                case 3 -> {
                    System.out.println("Chiusura applicazione.");
                    System.exit(0);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void login() {
        try {
            credentials = LoginView.authenticate();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            //sovrascrivo credentials con il risultato della chiamata alla procedura logic
            credentials = new LoginProcedureDAO().execute(credentials.getUsername(), credentials.getPassword());
        } catch (DAOException e) {
            throw new RuntimeException(e);
        }
    }

    private void registration() {
        UtenteBase utente;
        CartaDiCredito cc;

        try {
            //Aggiungo un nuovo utente base
            utente = LoginView.addNewUserForm();

            //Aggiungo una nuova carta di credito
            cc = LoginView.addCreditCardInfoForm();

            //Eseguo la procedura per memorizzare nel database il nuovo utente registrato insieme alla carta di credito associata
            System.out.print("\n++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n");
            System.out.println(new RegistraUtenteDAO().execute(utente, cc));
            System.out.print("\n++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n");

        } catch (IOException | DAOException e) {
            e.printStackTrace();
        }
    }



}
