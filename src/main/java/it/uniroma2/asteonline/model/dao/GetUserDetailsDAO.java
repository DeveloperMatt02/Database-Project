package it.uniroma2.asteonline.model.dao;

import it.uniroma2.asteonline.exception.DAOException;
import it.uniroma2.asteonline.factory.ConnectionFactory;
import it.uniroma2.asteonline.model.domain.*;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GetUserDetailsDAO  implements GenericProcedureDAO<Utente> {

    @Override
    public Utente execute(Object... params) throws DAOException {
        //check iniziale per avviare la procedura correttamente
        if (params.length != 2 || !(params[0] instanceof String) || !(params[1] instanceof Role)) {
            throw new DAOException("Parametri non validi");
        }

        String cf = (String) params[0];
        Role ruolo = (Role) params[1];

        try (Connection conn = ConnectionFactory.getConnection(); CallableStatement stmt = conn.prepareCall("{call dettagli_utente_cf(?,?)}")) {

            stmt.setString(1, cf);
            stmt.setString(2, ruolo.name());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    if (ruolo == Role.USER) {
                        // Leggi i campi extra dell'utente base
                        UtenteBase ub = new UtenteBase();
                        ub.setCodiceFiscale(rs.getString("CF"));
                        ub.setNome(rs.getString("Nome"));
                        ub.setCognome(rs.getString("Cognome"));
                        ub.setIndirizzo(rs.getString("Indirizzo"));
                        ub.setCAP(rs.getString("CAP"));
                        ub.setCitta(rs.getString("Città"));
                        ub.setDataNascita(rs.getDate("DataNascita").toLocalDate());
                        ub.setCittaNascita(rs.getString("CittàNascita"));
                        ub.setCartaCredito(rs.getString("CartaCredito"));
                        return ub;
                    } else {
                        // Utente amministratore generico
                        Utente utente = new Utente();
                        utente.setCodiceFiscale(rs.getString("CF"));
                        utente.setNome(rs.getString("Nome"));
                        utente.setCognome(rs.getString("Cognome"));
                        return utente;
                    }
                } else {
                    //non dovrebbe verificarsi mai!
                    throw new DAOException("Nessun utente trovato con codice fiscale " + cf);
                }
            }

        } catch (SQLException e) {
            throw new DAOException("Errore durante l'esecuzione della procedura", e);
        }
    }
}
