package it.uniroma2.asteonline.model.dao;

import it.uniroma2.asteonline.exception.DAOException;
import it.uniroma2.asteonline.factory.ConnectionFactory;
import it.uniroma2.asteonline.model.domain.Categoria;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

public class AggiungiCategoriaDAO implements GenericProcedureDAO<String>{

    @Override
    public String execute(Object... params) throws DAOException {
        Categoria categoria = (Categoria) params[0];

        try (Connection conn = ConnectionFactory.getConnection();
             CallableStatement cs = conn.prepareCall("{call aggiungiCat(?,?,?)}")) {

            cs.setString(1, categoria.getNomeCategoria());
            cs.setInt(2, categoria.getLivello());
            cs.setString(3, categoria.getNomeCategoria());
            cs.execute();

        } catch (SQLException e) {
            throw new DAOException("Errore " + e.getSQLState() + ": aggiunta nuova categoria fallita a causa del seguente errore. " + e.getMessage());
        }


        return "\nCategoria aggiunta con successo al database!";
    }
}
