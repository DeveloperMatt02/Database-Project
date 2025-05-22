package it.uniroma2.asteonline.model.dao;

import it.uniroma2.asteonline.exception.DAOException;
import it.uniroma2.asteonline.factory.ConnectionFactory;
import it.uniroma2.asteonline.model.domain.Offerta;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class VisualizzaOfferteAstaDAO implements GenericProcedureDAO<List<Offerta>>{


    @Override
    public List<Offerta> execute(Object... params) throws DAOException {
        int idAsta = (Integer) params[0];
        boolean soloAutomatiche = (boolean) params[1];

        List<Offerta> off = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             CallableStatement cs = conn.prepareCall("{call visualizza_offerte_asta(?, ?)}")) {

            cs.setInt(1, idAsta);
            cs.setBoolean(2, soloAutomatiche);

            try (ResultSet rs = cs.executeQuery()) {
                while(rs.next()) {
                    Offerta o = new Offerta();

                    o.setUtenteBase(rs.getString("UtenteBase"));
                    o.setAsta(rs.getInt("Asta"));
                    o.setData(rs.getDate("Data").toLocalDate());
                    o.setOra(rs.getTime("Ora").toLocalTime());
                    o.setImporto(rs.getBigDecimal("Importo"));
                    o.setAutomatica(rs.getBoolean("Automatica"));

                    off.add(o);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Errore " + e.getSQLState() + ": recupero delle offerte della seguente asta fallito a causa del seguente errore. " + e.getMessage());
        }

        return off;
    }
}
