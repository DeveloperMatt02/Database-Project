package it.uniroma2.asteonline.model.dao;

import it.uniroma2.asteonline.exception.DAOException;
import it.uniroma2.asteonline.factory.ConnectionFactory;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

public class EliminaCategoriaDAO implements GenericProcedureDAO<String>{

    @Override
    public String execute(Object... params) throws DAOException {
        String catDelName = (String) params[0];

        Integer num_del;
        Integer num_reassigned;

        try (Connection conn = ConnectionFactory.getConnection();
             CallableStatement cs = conn.prepareCall("{call eliminaCat(?,?,?)}")) {

            cs.setString(1, catDelName);
            cs.registerOutParameter(2, Types.INTEGER);
            cs.registerOutParameter(3, Types.INTEGER);
            cs.executeQuery();

            num_del = cs.getInt(2);
            num_reassigned = cs.getInt(3);
        } catch (SQLException e) {
            throw new DAOException("Errore " + e.getSQLState() + ": eliminazione categoria fallita a causa del seguente errore. " + e.getMessage());
        }

        return "\nSono state eliminate " + num_del + " categorie e sono state riassegnate alla categoria di default " + num_reassigned + " aste.";
    }

}
