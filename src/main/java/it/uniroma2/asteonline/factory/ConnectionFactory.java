package it.uniroma2.asteonline.factory;

import it.uniroma2.asteonline.model.domain.Role;
import it.uniroma2.asteonline.utils.LoggedUser;

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

    static { //connessione iniziale
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

    //carico le proprietà
    public static void loadProperties(String type) {
        try (InputStream input = new FileInputStream("resources/db.properties")) {
            Properties properties = new Properties();
            properties.load(input);

            connection_url = properties.getProperty("CONNECTION_URL");
            user = properties.getProperty(type + "_USER");
            pass = properties.getProperty(type + "_PASSWORD");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            //carico le proprietà per la futura connessione
            if(LoggedUser.getRole() == null) {
                loadProperties("LOGIN");
            } else {
                switch (LoggedUser.getRole().name()) {
                    case "USER" -> loadProperties("USER");
                    case "ADMIN" -> loadProperties("ADMIN");
                }
            }

            //riapro la connessione dopo il logout
            connection = DriverManager.getConnection(connection_url, user, pass);
        }

        return connection;
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
