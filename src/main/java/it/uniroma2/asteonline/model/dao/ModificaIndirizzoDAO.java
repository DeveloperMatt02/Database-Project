package it.uniroma2.asteonline.model.dao;

import it.uniroma2.asteonline.exception.DAOException;
import it.uniroma2.asteonline.factory.ConnectionFactory;
import it.uniroma2.asteonline.model.domain.UtenteBase;
import it.uniroma2.asteonline.utils.LoggedUser;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

public class ModificaIndirizzoDAO implements GenericProcedureDAO<Integer>{

    @Override
    public Integer execute(Object... params) throws DAOException {
        UtenteBase utente = (UtenteBase) params[0];
        int result;

        try (Connection conn = ConnectionFactory.getConnection(); CallableStatement cs = conn.prepareCall("{call aggiorna_indirizzo_consegna(?,?,?,?,?)}")) {

            //prendo il codice fiscale per identificare la chiave primaria
            cs.setString(1, LoggedUser.getCF());

            //prendo gli altri dati da aggiornare
            cs.setString(2, utente.getIndirizzo());
            cs.setString(3, utente.getCitta());
            cs.setString(4, utente.getCAP());

            //intero come valore da restituire
            cs.registerOutParameter(5, java.sql.Types.INTEGER);
            cs.executeQuery();
            result = cs.getInt(5);

        } catch (SQLException e) {
            throw new DAOException("Errore durante l'esecuzione della procedura", e);
        }

        return result;
    }
}
