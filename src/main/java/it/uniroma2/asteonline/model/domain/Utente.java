package it.uniroma2.asteonline.model.domain;

public class Utente {
    protected String codiceFiscale;
    protected String nome;
    protected String cognome;

    public Utente() {}

    public void setCodiceFiscale(String CF) {
        this.codiceFiscale = CF;
    }

    public String getCodiceFiscale() {
        return this.codiceFiscale;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return this.nome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String getCognome() {
        return this.cognome;
    }
}
