package it.uniroma2.asteonline.controller;

import it.uniroma2.asteonline.model.domain.Credentials;
import it.uniroma2.asteonline.model.domain.Utente;
import it.uniroma2.asteonline.model.domain.UtenteBase;
import it.uniroma2.asteonline.utils.LoggedUser;

public class MainController implements Controller{
    Credentials cred;
    Utente loggedUser;
    LoginController loginController = new LoginController();

    @Override
    public void start() {
        while (true) {

            loginController.start();
            cred = loginController.getCred();


            if(cred.getRole() == null) {
                System.out.println("Errore: credenziali errate.");
                continue; //torno al login
            }

            //Imposto i dati associati alla sessione loggata
            initializeSession(cred);

            //Avvio la CLI specifica per il tipo di utente
            switch(LoggedUser.getRole()) {
                case USER -> new UserController().start();
                case ADMIN -> new AdminController().start();
                default -> System.out.println("Errore: ruolo non riconosciuto.");
            }

            //dopo avere effettuato il logout si torna all'inizio del ciclo while
            System.out.println("Sessione terminata. Torno alla schermata di login.\n");

            //pulisco le informazioni memorizzate nella sessione corrente
            LoggedUser.clear();
        }
    }

    private void initializeSession(Credentials cred) {
        //una volta verificate le credenziali di accesso inizializzo l'utente loggato
        loggedUser = loginController.getLoggedUser();

        LoggedUser.setUsername(loggedUser.getUsername());
        LoggedUser.setPassword(loggedUser.getPassword());
        LoggedUser.setCF(loggedUser.getCodiceFiscale());
        LoggedUser.setRole(cred.getRole());
        LoggedUser.setNome(loggedUser.getNome());
        LoggedUser.setCognome(loggedUser.getCognome());

        if (loggedUser instanceof UtenteBase ub) {
            LoggedUser.setIndirizzo(ub.getIndirizzo());
            LoggedUser.setCap(ub.getCAP());
            LoggedUser.setCitta(ub.getCitta());
            LoggedUser.setDataNascita(ub.getDataNascita());
            LoggedUser.setCittaNascita(ub.getCittaNascita());
            LoggedUser.setNumeroCarta(ub.getCartaCredito());
        }

    }
}
