package it.uniroma2.asteonline.model.dao;

import it.uniroma2.asteonline.exception.DAOException;
import it.uniroma2.asteonline.factory.ConnectionFactory;
import it.uniroma2.asteonline.model.domain.Categoria;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ListaCategorieDAO implements GenericProcedureDAO<List<Categoria>>{

    @Override
    public List<Categoria> execute(Object... params) throws DAOException {
        List<Categoria> fullCatList = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             CallableStatement cs = conn.prepareCall("{call listaCategorie()}")) {
            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    //costruisco ciascuna categoria ottenuta
                    Categoria cat = new Categoria();
                    cat.setNomeCategoria(rs.getString("Nome"));
                    cat.setLivello(rs.getInt("Livello"));
                    cat.setCategoriaSuperiore(rs.getString("CategoriaSuperiore"));

                    //aggiungo la categoria costruita alla lista
                    fullCatList.add(cat);
                }
            }

        } catch (SQLException e) {
            throw new DAOException("Errore " + e.getSQLState() + ": fetch delle categorie fallito a causa del seguente errore. " + e.getMessage());
        }

        return fullCatList;
    }
}
