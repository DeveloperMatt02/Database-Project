package it.uniroma2.asteonline.model.dao;

import it.uniroma2.asteonline.exception.DAOException;
import it.uniroma2.asteonline.factory.ConnectionFactory;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

public class ChiudiAstaDAO implements GenericProcedureDAO<String>{

    @Override
    public String execute(Object... params) throws DAOException {
        Integer idAsta = (Integer) params[0];

        try (Connection conn = ConnectionFactory.getConnection();
             CallableStatement cs = conn.prepareCall("{call chiudiAsta(?)}")) {

            cs.setInt(1, idAsta);
            cs.execute();
        } catch (SQLException e) {
            throw new DAOException("Errore " + e.getSQLState() + ": chiusura asta fallita a causa del seguente errore. " + e.getMessage());
        }

        return "\nAsta terminata con successo!";
    }
}
