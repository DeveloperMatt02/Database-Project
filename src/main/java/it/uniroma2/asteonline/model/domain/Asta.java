package it.uniroma2.asteonline.model.domain;

import it.uniroma2.asteonline.utils.LoggedUser;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Asta {
    private int id;
    private String dimensioni;
    private LocalDateTime data;
    private int durata;
    private String descrizione;
    private BigDecimal prezzoBase;
    private String statoAsta;
    private int numOfferte;
    private BigDecimal offertaMassima;
    private String condizioniArticolo;
    private String categoria;
    private String utenteAmministratore;
    private String utenteBase;

    //campi aggiuntivi
    private long tempoRimanenteSec;
    private BigDecimal importoOffertaUtente;
    private LocalDate dataOffertaUtente;
    private LocalTime oraOffertaUtente;
    private boolean isControfferta;
    private BigDecimal importoControfferta;


    public Asta() {}

    // Getter e Setter

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDimensioni() {
        return dimensioni;
    }

    public void setDimensioni(String dimensioni) {
        this.dimensioni = dimensioni;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public int getDurata() {
        return durata;
    }

    public void setDurata(int durata) {
        this.durata = durata;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public BigDecimal getPrezzoBase() {
        return prezzoBase;
    }

    public void setPrezzoBase(BigDecimal prezzoBase) {
        this.prezzoBase = prezzoBase;
    }

    public String getStatoAsta() {
        return statoAsta;
    }

    public void setStatoAsta(String statoAsta) {
        this.statoAsta = statoAsta;
    }

    public int getNumOfferte() {
        return numOfferte;
    }

    public void setNumOfferte(int numOfferte) {
        this.numOfferte = numOfferte;
    }

    public BigDecimal getOffertaMassima() {
        return offertaMassima;
    }

    public void setOffertaMassima(BigDecimal offertaMassima) {
        this.offertaMassima = offertaMassima;
    }

    public String getCondizioniArticolo() {
        return condizioniArticolo;
    }

    public void setCondizioniArticolo(String condizioniArticolo) {
        this.condizioniArticolo = condizioniArticolo;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getUtenteAmministratore() {
        return utenteAmministratore;
    }

    public void setUtenteAmministratore(String utenteAmministratore) {
        this.utenteAmministratore = utenteAmministratore;
    }

    public String getUtenteBase() {
        return utenteBase;
    }

    public void setUtenteBase(String utenteBase) {
        this.utenteBase = utenteBase;
    }

    //setter e getter aggiuntivi

    public long getTempoRimanenteSec() {
        return tempoRimanenteSec;
    }

    public void setTempoRimanenteSec(long tempoRimanenteSec) {
        this.tempoRimanenteSec = tempoRimanenteSec;
    }

    public BigDecimal getImportoOffertaUtente() {
        return importoOffertaUtente;
    }

    public void setImportoOffertaUtente(BigDecimal importoOffertaUtente) {
        this.importoOffertaUtente = importoOffertaUtente;
    }

    public LocalDate getDataOffertaUtente() {
        return dataOffertaUtente;
    }

    public void setDataOffertaUtente(LocalDate dataOffertaUtente) {
        this.dataOffertaUtente = dataOffertaUtente;
    }

    public LocalTime getOraOffertaUtente() {
        return oraOffertaUtente;
    }

    public void setOraOffertaUtente(LocalTime oraOffertaUtente) {
        this.oraOffertaUtente = oraOffertaUtente;
    }

    public void setImportoControfferta(BigDecimal importoControfferta) {
        this.importoControfferta = importoControfferta;
    }

    public void setAutomatica(boolean controfferta) {
        isControfferta = controfferta;
    }

    public boolean isControfferta() {
        return isControfferta;
    }

    public BigDecimal getImportoControfferta() {
        return importoControfferta;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        String utenteVincitore = this.utenteBase == null ? "--- asta in corso ---" : this.utenteBase;
        String durataText = this.durata == 1 ? "giorno" : "giorni";

        sb.append("\n")
                .append("ID: ").append(id).append("\n")
                .append("Venditore: ").append(utenteAmministratore).append(" | ").append(LoggedUser.getNome()).append(" | ").append(LoggedUser.getCognome()).append("\n")
                .append("Vincitore: ").append(utenteVincitore).append("\n")
                .append("Dimensioni: ").append(dimensioni).append("cm\n")
                .append("Data: ").append(data.toString()).append("\n")
                .append("Durata: ").append(durata).append(durataText).append("\n")
                .append("Descrizione: ").append(descrizione).append("\n")
                .append("Prezzo base: €").append(prezzoBase).append("\n")
                .append("Stato asta: ").append(statoAsta).append("\n")
                .append("# offerte: ").append(numOfferte).append("\n")
                .append("Offerta massima: ").append(offertaMassima).append("\n")
                .append("Condizioni articolo: ").append(condizioniArticolo).append("\n")
                .append("Categoria: ").append(categoria);

        return sb.toString();
    }
}
