package it.uniroma2.asteonline.model.dao;

import it.uniroma2.asteonline.exception.DAOException;
import it.uniroma2.asteonline.factory.ConnectionFactory;
import it.uniroma2.asteonline.model.domain.Offerta;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

public class AggiungiOffertaDAO implements GenericProcedureDAO<String>{
    @Override
    public String execute(Object... params) throws DAOException {

        Offerta offerta = (Offerta) params[0];

        try (Connection conn = ConnectionFactory.getConnection();
             CallableStatement cs = conn.prepareCall("{call aggiungiOfferta(?,?,?,?,?)}")) {

            cs.setString(1, offerta.getUtenteBase());                     // var_utenteBase
            cs.setInt(2, offerta.getAsta());                              // var_idAsta
            cs.setBigDecimal(3, offerta.getImporto());                    // var_importo
            cs.setBoolean(4, offerta.isAutomatica());                     // var_automatica
            if (offerta.getImportoControfferta() != null) {
                cs.setBigDecimal(5, offerta.getImportoControfferta());    // var_maxControfferta
            } else {
                cs.setNull(5, java.sql.Types.DECIMAL);
            }

            cs.execute();


            //System.out.println("Offerta inserita per utente " + offerta.getUtenteBase() + ", asta " + offerta.getAsta());
        } catch (SQLException e) {
            throw new DAOException("Errore " + e.getSQLState() + ": inserimento offerta fallito. " + e.getMessage());
        }

        return "\nOfferta aggiunta con successo al database!";
    }
}
