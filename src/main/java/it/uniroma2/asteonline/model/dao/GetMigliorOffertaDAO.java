package it.uniroma2.asteonline.model.dao;

import it.uniroma2.asteonline.exception.DAOException;
import it.uniroma2.asteonline.factory.ConnectionFactory;
import it.uniroma2.asteonline.model.domain.Offerta;

import java.sql.*;

public class GetMigliorOffertaDAO implements GenericProcedureDAO<Offerta> {
    @Override
    public Offerta execute(Object... params) throws DAOException {
        int idAsta = (Integer) params[0];
        Offerta offerta = null;

        try (Connection conn = ConnectionFactory.getConnection();
             CallableStatement cs = conn.prepareCall("{call getMigliorOfferta(?)}")) {

            cs.setInt(1, idAsta);
            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    offerta = new Offerta();
                    offerta.setUtenteBase(rs.getString("UtenteBase"));
                    offerta.setAsta(rs.getInt("Asta"));
                    offerta.setImporto(rs.getBigDecimal("Importo"));
                    offerta.setAutomatica(rs.getBoolean("Automatica"));
                    offerta.setImportoControfferta(rs.getBigDecimal("ImportoControfferta"));
                    offerta.setData(rs.getDate("Data").toLocalDate());
                    offerta.setOra(rs.getTime("Ora").toLocalTime());
                }
            }

        } catch (SQLException e) {
            throw new DAOException("Errore durante il recupero dell'offerta massima: " + e.getMessage());
        }

        return offerta;
    }
}
