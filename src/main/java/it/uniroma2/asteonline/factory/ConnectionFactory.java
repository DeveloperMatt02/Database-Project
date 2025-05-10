package it.uniroma2.asteonline.factory;

import it.uniroma2.asteonline.model.domain.Role;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionFactory {
    private static Connection connection;
    private static String connection_url;
    private static String user;
    private static String pass;

    private ConnectionFactory() {}


    /*
    static {
        //loadProperties();

        try (InputStream input = new FileInputStream("resources/db.properties")) {
            Properties properties = new Properties();
            properties.load(input);

            String connection_url = properties.getProperty("CONNECTION_URL");
            String user = properties.getProperty("LOGIN_USER");
            String pass = properties.getProperty("LOGIN_PASSWORD");

            connection = DriverManager.getConnection(connection_url, user, pass);
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }


    }

    */

    //carico le proprietà
    public static void loadProperties() {
        try (InputStream input = new FileInputStream("resources/db.properties")) {
            Properties properties = new Properties();
            properties.load(input);

            connection_url = properties.getProperty("CONNECTION_URL");
            user = properties.getProperty("LOGIN_USER");
            pass = properties.getProperty("LOGIN_PASSWORD");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            //carico le proprietà
            loadProperties();

            //riapro la connessione dopo il logout
            connection = DriverManager.getConnection(connection_url, user, pass);
        }

        return connection;
    }

    public static void changeRole(Role role) throws SQLException {
        //prima di cambiare ruolo mi assicuro di chiudere la connessione corrente
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }

        //carico le proprietà con il nuovo ruolo (input)
        try (InputStream input = new FileInputStream("resources/db.properties")) {
            Properties properties = new Properties();
            properties.load(input);

            String connection_url = properties.getProperty("CONNECTION_URL");
            String user = properties.getProperty(role.name() + "_USER");
            String pass = properties.getProperty(role.name() + "_PASSWORD");

            connection = DriverManager.getConnection(connection_url, user, pass);
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static void closeConnection() throws SQLException {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            throw new SQLException();
        }
    }

}
