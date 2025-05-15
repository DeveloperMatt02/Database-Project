package it.uniroma2.asteonline.model.dao;

import it.uniroma2.asteonline.exception.DAOException;
import it.uniroma2.asteonline.factory.ConnectionFactory;
import it.uniroma2.asteonline.model.domain.CartaDiCredito;
import it.uniroma2.asteonline.model.domain.UtenteBase;

import java.sql.*;

public class RegistraUtenteDAO implements GenericProcedureDAO<String>{
    @Override
    public String execute(Object... params) throws DAOException {
        UtenteBase utente = (UtenteBase) params[0];
        CartaDiCredito cartadicredito = (CartaDiCredito) params[1];

        try (Connection conn = ConnectionFactory.getConnection();
             CallableStatement cs = conn.prepareCall("{call registerUser(?,?,?,?,?,?,?,?,?,?,?,?,?)}");){

            cs.setString(1, utente.getUsername());
            cs.setString(2, utente.getPassword());
            cs.setString(3, utente.getCodiceFiscale());
            cs.setString(4, utente.getNome());
            cs.setString(5, utente.getCognome());
            cs.setString(6, utente.getIndirizzo());
            cs.setString(7, utente.getCAP());
            cs.setString(8, utente.getCitta());
            cs.setDate(9, Date.valueOf(utente.getDataNascita()));
            cs.setString(10, utente.getCittaNascita());
            cs.setString(11, cartadicredito.getNumeroCarta());
            cs.setString(12, cartadicredito.getCVV());
            cs.setDate(13, Date.valueOf(cartadicredito.getDataScadenza()));

            cs.execute();
        } catch (SQLException e) {
            throw new DAOException("Errore: utente non registrato a causa del seguente errore -> " + e.getMessage() + "\nRiprovare a seguire la procedura.");
        }

        return "\nUtente " + utente.getUsername() + " registrato con successo!";
    }
}
