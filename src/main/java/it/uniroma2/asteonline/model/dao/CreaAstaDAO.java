package it.uniroma2.asteonline.model.dao;

import it.uniroma2.asteonline.exception.DAOException;
import it.uniroma2.asteonline.factory.ConnectionFactory;
import it.uniroma2.asteonline.model.domain.Asta;
import it.uniroma2.asteonline.model.domain.Categoria;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;

public class CreaAstaDAO implements GenericProcedureDAO<String> {

    @Override
    public String execute(Object... params) throws DAOException {
        Asta asta = (Asta) params[0];
        Categoria categoria = (Categoria) params[1];

        try (Connection conn = ConnectionFactory.getConnection();
             CallableStatement cs = conn.prepareCall("{call creaAsta(?,?,?,?,?,?,?,?,?)}")) {

            cs.setString(1, asta.getDimensioni());
            cs.setTimestamp(2, Timestamp.valueOf(asta.getData()));
            cs.setInt(3, asta.getDurata());
            cs.setString(4, asta.getDescrizione());
            cs.setBigDecimal(5, asta.getPrezzoBase());
            cs.setString(6, asta.getStatoAsta());
            cs.setString(7, asta.getCondizioniArticolo());
            cs.setString(8, categoria.getNomeCategoria());
            cs.setString(9, asta.getUtenteAmministratore());
            cs.execute();

        } catch (SQLException e) {
            throw new DAOException("Errore " + e.getSQLState() + ": registrazione nuova asta fallita a causa del seguente errore. " + e.getMessage());
        }

        return "\nAsta aggiunta con successo al database!";
    }
}
