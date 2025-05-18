package it.uniroma2.asteonline.model.dao;

import it.uniroma2.asteonline.exception.DAOException;
import it.uniroma2.asteonline.factory.ConnectionFactory;
import it.uniroma2.asteonline.model.domain.Asta;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class VisualizzaAstePartecipateDAO implements GenericProcedureDAO<List<Asta>>{
    @Override
    public List<Asta> execute(Object... params) throws DAOException {
        String cf = (String) params[0];

        List<Asta> pAste = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             CallableStatement cs = conn.prepareCall("{call visualizzaAstePartecipate(?)}")) {

            cs.setString(1, cf);

            try (ResultSet rs = cs.executeQuery()) {
                while(rs.next()) {
                    Asta pA = new Asta();

                    pA.setId(rs.getInt("ID"));
                    pA.setDimensioni(rs.getString("Dimensioni"));
                    pA.setData(rs.getTimestamp("Data").toLocalDateTime()); // usa getDate() se hai java.sql.Date
                    pA.setDescrizione(rs.getString("Descrizione"));
                    pA.setDurata(rs.getInt("Durata"));
                    pA.setNumOfferte(rs.getInt("NumOfferte"));
                    pA.setOffertaMassima(rs.getBigDecimal("OffertaMassima"));
                    pA.setStatoAsta(rs.getString("StatoAsta"));
                    pA.setCondizioniArticolo(rs.getString("CondizioniArticolo"));
                    pA.setCategoria(rs.getString("Categoria"));
                    pA.setUtenteAmministratore(rs.getString("UtenteAmministratore"));
                    pA.setPrezzoBase(rs.getBigDecimal("PrezzoBase"));

                    pA.setTempoRimanenteSec(rs.getLong("TempoRimanenteSec"));
                    pA.setImportoOffertaUtente(rs.getBigDecimal("ImportoUltimaOfferta"));
                    pA.setDataOffertaUtente(rs.getDate("DataUltimaOfferta").toLocalDate());
                    pA.setOraOffertaUtente(rs.getTime("OraUltimaOfferta").toLocalTime());

                    pAste.add(pA);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Errore " + e.getSQLState() + ": visualizzazione aste partecipate fallita a causa del seguente errore. " + e.getMessage());
        }

        return pAste;
    }
}
