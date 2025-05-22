package it.uniroma2.asteonline.model.dao;

import it.uniroma2.asteonline.exception.DAOException;
import it.uniroma2.asteonline.factory.ConnectionFactory;
import it.uniroma2.asteonline.model.domain.Asta;
import it.uniroma2.asteonline.model.domain.Categoria;
import it.uniroma2.asteonline.model.domain.Utente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VisualizzaAsteFiltrateDAO implements GenericProcedureDAO<List<Asta>>{
    @Override
    public List<Asta> execute(Object... params) throws DAOException {
        Categoria categoria = (Categoria) params[0];
        Utente amministratore = (Utente) params[1];

        List<Asta> asteAttive = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             CallableStatement cs = conn.prepareCall("{call visualizzaAsteFiltrate(?,?)}")) {

            if (categoria != null) {
                cs.setString(1, categoria.getNomeCategoria());
            } else {
                cs.setNull(1, Types.VARCHAR);
            }

            if (amministratore != null) {
                cs.setString(2, amministratore.getCodiceFiscale());
            } else {
                cs.setNull(2, Types.CHAR);
            }

            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    Asta a = new Asta();

                    a.setId(rs.getInt("ID"));
                    a.setDimensioni(rs.getString("Dimensioni"));
                    a.setData(rs.getTimestamp("Data").toLocalDateTime()); // usa getDate() se hai java.sql.Date
                    a.setDescrizione(rs.getString("Descrizione"));
                    a.setDurata(rs.getInt("Durata"));
                    a.setNumOfferte(rs.getInt("NumOfferte"));
                    a.setOffertaMassima(rs.getBigDecimal("OffertaMassima"));
                    a.setStatoAsta(rs.getString("StatoAsta"));
                    a.setCondizioniArticolo(rs.getString("CondizioniArticolo"));
                    a.setCategoria(rs.getString("Categoria"));
                    a.setUtenteAmministratore(rs.getString("UtenteAmministratore"));
                    a.setPrezzoBase(rs.getBigDecimal("PrezzoBase"));

                    //aggiungo anche il tempo rimanente alla chiusura dell'asta
                    a.setTempoRimanenteSec(rs.getLong("TempoRimanenteSec"));

                    asteAttive.add(a);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Errore " + e.getSQLState() + ": visualizzazione aste fallita a causa del seguente errore. " + e.getMessage());
        }

        return asteAttive;
    }
}
