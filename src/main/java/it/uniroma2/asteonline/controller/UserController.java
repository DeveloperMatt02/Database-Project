package it.uniroma2.asteonline.controller;

import it.uniroma2.asteonline.exception.DAOException;
import it.uniroma2.asteonline.factory.ConnectionFactory;
import it.uniroma2.asteonline.model.dao.ModificaIndirizzoDAO;
import it.uniroma2.asteonline.model.domain.Role;
import it.uniroma2.asteonline.model.domain.UtenteBase;
import it.uniroma2.asteonline.utils.LoggedUser;
import it.uniroma2.asteonline.view.UserView;

import java.io.IOException;
import java.sql.SQLException;

public class UserController implements Controller {

    @Override
    public void start() {
        userHomepage();
    }

    //TODO:: utente base cosa deve fare???
    /*
        - visualizzare le aste attive (generali, filtrate per categoria o utente amministratore)
        - visualizzare i dettagli di una singola asta (non in questo menù) ed eventualmente fare un offerta (impostando un'offerta automatica oppure normale)
        - visualizzare l'elenco degli articoli aggiudicati
        - visualizzare le aste attive a cui si sta partecipando
        - fare logout
    */

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
        //TODO::
    }


    private void elencoArticoliAggiudicati() {
        //TODO::
    }

    private void elencoAstePartecipate() {
        //TODO::
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

            if(result == 0) {
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
