package it.uniroma2.asteonline.model.dao;

import it.uniroma2.asteonline.exception.DAOException;
import it.uniroma2.asteonline.factory.ConnectionFactory;
import it.uniroma2.asteonline.model.domain.Offerta;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GetControfferteAttiveDAO implements GenericProcedureDAO<List<Offerta>> {
    @Override
    public List<Offerta> execute(Object... params) throws DAOException {
        int idAsta = (Integer) params[0];
        String migliorOfferente = (String) params[1];
        BigDecimal importoAttuale = (BigDecimal) params[2];

        List<Offerta> controfferte = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             CallableStatement cs = conn.prepareCall("{call getControfferteAttive(?, ?, ?)}")) {

            cs.setInt(1, idAsta);
            cs.setString(2, migliorOfferente);
            cs.setBigDecimal(3, importoAttuale);

            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    Offerta offerta = new Offerta();
                    offerta.setUtenteBase(rs.getString("UtenteBase"));
                    offerta.setAsta(rs.getInt("Asta"));
                    offerta.setImporto(rs.getBigDecimal("Importo"));
                    offerta.setAutomatica(rs.getBoolean("Automatica"));
                    offerta.setImportoControfferta(rs.getBigDecimal("ImportoControfferta"));
                    offerta.setData(rs.getDate("Data").toLocalDate());
                    offerta.setOra(rs.getTime("Ora").toLocalTime());
                    controfferte.add(offerta);
                }
            }

        } catch (SQLException e) {
            throw new DAOException("Errore durante il recupero delle controfferte attive: " + e.getMessage());
        }

        return controfferte;
    }
}
