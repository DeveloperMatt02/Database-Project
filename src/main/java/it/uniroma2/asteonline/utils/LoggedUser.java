package it.uniroma2.asteonline.utils;

import it.uniroma2.asteonline.model.domain.Role;

import java.time.LocalDate;

public class LoggedUser {
    private static String username;
    private static String password;
    private static Role role;
    private static String cf;

    //dati personali (generico)
    private static String nome;
    private static String cognome;

    //indirizzo di consegna (solo utenti base)
    private static String indirizzo;
    private static String cap;
    private static String citta;

    //altri dati (solo utenti base)
    private static LocalDate dataNascita;
    private static String cittaNascita;
    private static String numeroCarta;

    private LoggedUser(){}

    //setter
    public static void setUsername(String username) {
        LoggedUser.username = username;
    }
    public static void setPassword(String password) {
        LoggedUser.password = password;
    }
    public static void setRole(Role role) {
        LoggedUser.role = role;
    }
    public static void setCF(String CF) {LoggedUser.cf = CF;}

    public static void setNome(String nome) {LoggedUser.nome = nome;}
    public static void setCognome(String cognome) {LoggedUser.cognome = cognome;}

    public static void setIndirizzo(String indirizzo) { LoggedUser.indirizzo = indirizzo;}
    public static void setCap(String cap) {LoggedUser.cap = cap;}
    public static void setCitta(String citta) {LoggedUser.citta = citta;}

    public static void setDataNascita(LocalDate dataNascita) { LoggedUser.dataNascita = dataNascita;}
    public static void setCittaNascita(String cittaNascita) { LoggedUser.cittaNascita = cittaNascita;}
    public static void setNumeroCarta(String numeroCarta) { LoggedUser.numeroCarta = numeroCarta;}

    //getter
    public static String getUsername() {
        return username;
    }
    public static String getPassword() {
        return password;
    }
    public static Role getRole() {
        return role;
    }
    public static String getCF() { return cf; }

    public static String getNome() { return nome; }
    public static String getCognome() { return cognome; }

    public static String getIndirizzo() { return indirizzo; }
    public static String getCap() { return cap; }
    public static String getCitta() { return citta; }

    public static LocalDate getDataNascita() { return dataNascita; }
    public static String getCittaNascita() { return cittaNascita; }
    public static String getNumeroCarta() { return numeroCarta; }

    public static void clear() {
        username = null;
        password = null;
        role = null;
        cf = null;

        nome = null;
        cognome = null;

        indirizzo = null;
        cap = null;
        citta = null;

        dataNascita = null;
        cittaNascita = null;
        numeroCarta = null;
    }
}
