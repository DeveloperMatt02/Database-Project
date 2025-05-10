package it.uniroma2.asteonline.model.domain;

import java.time.LocalDate;

public class UtenteBase extends Utente {
    private String indirizzo;
    private String CAP;
    private String citta;
    private LocalDate dataNascita;
    private String cittaNascita;

    private String cartaCredito;

    public UtenteBase() {}

    public void setCAP(String CAP) {
        this.CAP = CAP;
    }

    public void setCitta(String citta) {
        this.citta = citta;
    }

    public void setCittaNascita(String cittaNascita) {
        this.cittaNascita = cittaNascita;
    }

    public void setDataNascita(LocalDate dataNascita) {
        this.dataNascita = dataNascita;
    }

    public void setIndirizzo(String indirizzo) {
        this.indirizzo = indirizzo;
    }

    public void setCartaCredito(String cartaCredito) { this.cartaCredito = cartaCredito; }

    public LocalDate getDataNascita() {
        return this.dataNascita;
    }

    public String getCAP() {
        return this.CAP;
    }

    public String getCitta() {
        return this.citta;
    }

    public String getIndirizzo() {
        return this.indirizzo;
    }

    public String getCittaNascita(){
        return this.cittaNascita;
    }

    public String getCartaCredito(){ return this.cartaCredito; }

}
