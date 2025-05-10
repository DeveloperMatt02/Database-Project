package it.uniroma2.asteonline.model.dao;

import it.uniroma2.asteonline.exception.DAOException;
import it.uniroma2.asteonline.factory.ConnectionFactory;
import it.uniroma2.asteonline.model.domain.Credentials;
import it.uniroma2.asteonline.model.domain.Role;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

public class LoginProcedureDAO implements GenericProcedureDAO<Credentials>{
    @Override
    public Credentials execute(Object... params) throws DAOException {
        String username = (String) params[0];
        String password = (String) params[1];
        String role;
        String cf;

        Role loggedRole = null;

        try {
            Connection conn = ConnectionFactory.getConnection();
            CallableStatement cs = conn.prepareCall("{call login(?,?,?,?)}");
            cs.setString(1, username);
            cs.setString(2, password);
            cs.registerOutParameter(3, Types.VARCHAR);
            cs.registerOutParameter(4, Types.VARCHAR);
            cs.executeQuery();
            role = cs.getString(3);
            cf = cs.getString(4);
        } catch (SQLException e) {
            throw new DAOException("Login error: " + e.getMessage());
        }

        if(role.equals(Role.ADMIN.toString())) {
            loggedRole = Role.ADMIN;
        } else if (role.equals(Role.USER.toString())) {
            loggedRole = Role.USER;
        }

        return new Credentials(username, password, loggedRole, cf);
    }
}
