package it.uniroma2.asteonline.utils;

import it.uniroma2.asteonline.model.domain.Role;

public class LoggedUser {
    private static String username;
    private static String password;
    private static Role role;

    private LoggedUser(){}

    public static void setUsername(String username) {
        LoggedUser.username = username;
    }
    public static void setPassword(String password) {
        LoggedUser.password = password;
    }
    public static void setRole(Role role) {
        LoggedUser.role = role;
    }

    public static String getUsername() {
        return username;
    }
    public static String getPassword() {
        return password;
    }
    public static Role getRole() {
        return role;
    }
}
