package it.uniroma2.asteonline.model.dao;

import it.uniroma2.asteonline.exception.DAOException;
import it.uniroma2.asteonline.factory.ConnectionFactory;
import it.uniroma2.asteonline.model.domain.Asta;
import it.uniroma2.asteonline.model.domain.Role;
import it.uniroma2.asteonline.utils.StatoAsta;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class VisualizzaAsteStatoDAO implements GenericProcedureDAO<List<Asta>>{

    @Override
    public List<Asta> execute(Object... params) throws DAOException {
        if (params.length != 2 || !(params[0] instanceof String) || !(params[1] instanceof StatoAsta)) {
            throw new DAOException("Parametri non validi");
        }

        String cf = (String) params[0];
        String stato = params[1].toString();

        List<Asta> storicoAste = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             CallableStatement cs = conn.prepareCall("{call visualizza_aste_stato(?,?)}")) {

            cs.setString(1, cf);
            cs.setString(2, stato);
            try (ResultSet rs = cs.executeQuery()) {
                while(rs.next()) {
                    Asta a = new Asta();

                    a.setId(rs.getInt("ID"));
                    a.setDimensioni(rs.getString("Dimensioni"));
                    a.setData(rs.getTimestamp("Data").toLocalDateTime()); // usa getDate() se hai java.sql.Date
                    a.setDurata(rs.getInt("Durata"));
                    a.setDescrizione(rs.getString("Descrizione"));
                    a.setPrezzoBase(rs.getBigDecimal("PrezzoBase"));
                    a.setStatoAsta(rs.getString("StatoAsta"));
                    a.setNumOfferte(rs.getInt("NumOfferte"));
                    a.setOffertaMassima(rs.getBigDecimal("OffertaMassima"));
                    a.setCondizioniArticolo(rs.getString("CondizioniArticolo"));
                    a.setCategoria(rs.getString("Categoria"));
                    a.setUtenteAmministratore(rs.getString("UtenteAmministratore"));
                    a.setUtenteBase(rs.getString("UtenteBase"));

                    storicoAste.add(a);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Errore " + e.getSQLState() + ": aggiunta nuova categoria fallita a causa del seguente errore. " + e.getMessage());
        }

        return storicoAste;
    }
}
