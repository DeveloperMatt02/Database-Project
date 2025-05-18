package it.uniroma2.asteonline.model.dao;

import it.uniroma2.asteonline.exception.DAOException;
import it.uniroma2.asteonline.factory.ConnectionFactory;
import it.uniroma2.asteonline.model.domain.Categoria;
import it.uniroma2.asteonline.model.domain.Utente;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GetAdminListDAO implements GenericProcedureDAO<List<Utente>>{
    @Override
    public List<Utente> execute(Object... params) throws DAOException {
        List<Utente> adminList = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             CallableStatement cs = conn.prepareCall("{call listaAmministratori()}")) {
            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    //costruisco ciascun utente amministratore
                    Utente admin = new Utente();

                    admin.setCodiceFiscale(rs.getString("CF"));
                    admin.setNome(rs.getString("Nome"));
                    admin.setCognome(rs.getString("Cognome"));

                    //aggiungo utente alla lista
                    adminList.add(admin);
                }
            }

        } catch (SQLException e) {
            throw new DAOException("Errore " + e.getSQLState() + ": fetch degli amministratori fallito a causa del seguente errore. " + e.getMessage());
        }

        return adminList;
    }
}
