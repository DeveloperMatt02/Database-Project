package it.uniroma2.asteonline.model.dao;

import it.uniroma2.asteonline.exception.DAOException;
import it.uniroma2.asteonline.factory.ConnectionFactory;
import it.uniroma2.asteonline.model.domain.Categoria;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

public class ModificaCategoriaDAO implements GenericProcedureDAO<String>{
    @Override
    public String execute(Object... params) throws DAOException {
        Categoria oldCat = (Categoria) params[0];
        Categoria modifiedCat = (Categoria) params[1];

        try (Connection conn = ConnectionFactory.getConnection();
             CallableStatement cs = conn.prepareCall("{call modificaCat(?,?)}")) {

            cs.setString(1, oldCat.getNomeCategoria());
            cs.setString(2, modifiedCat.getNomeCategoria());
            cs.execute();
        } catch (SQLException e) {
            throw new DAOException("Errore " + e.getSQLState() + ": aggiunta nuova categoria fallita a causa del seguente errore. " + e.getMessage());
        }


        return "\nNome categoria modificato con successo!";
    }


}
