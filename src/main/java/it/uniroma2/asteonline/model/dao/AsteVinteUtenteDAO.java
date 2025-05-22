package it.uniroma2.asteonline.model.dao;

import it.uniroma2.asteonline.exception.DAOException;
import it.uniroma2.asteonline.factory.ConnectionFactory;
import it.uniroma2.asteonline.model.domain.Asta;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AsteVinteUtenteDAO implements GenericProcedureDAO<List<Asta>>{
    @Override
    public List<Asta> execute(Object... params) throws DAOException {
        String cf = (String) params[0];

        List<Asta> asteAggiudicate = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             CallableStatement cs = conn.prepareCall("{call asteVinteUtente(?)}")) {

            cs.setString(1, cf);

            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    Asta a = new Asta();

                    a.setId(rs.getInt("ID"));
                    a.setDescrizione(rs.getString("Descrizione"));
                    a.setDimensioni(rs.getString("Dimensioni"));
                    a.setCondizioniArticolo(rs.getString("CondizioniArticolo"));
                    a.setCategoria(rs.getString("Categoria"));
                    a.setPrezzoBase(rs.getBigDecimal("PrezzoBase"));
                    a.setOffertaMassima(rs.getBigDecimal("OffertaMassima"));
                    a.setData(rs.getTimestamp("Data").toLocalDateTime());
                    a.setDurata(rs.getInt("Durata"));
                    a.setNumOfferte(rs.getInt("NumOfferte"));
                    a.setUtenteAmministratore(rs.getString("UtenteAmministratore"));

                    asteAggiudicate.add(a);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Errore " + e.getSQLState() + ": fetch aste aggiudicate fallito a causa del seguente errore. " + e.getMessage());
        }

        return asteAggiudicate;
    }
}


