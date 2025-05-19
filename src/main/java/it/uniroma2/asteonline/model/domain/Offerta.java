package it.uniroma2.asteonline.model.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public class Offerta {

    private String utenteBase;
    private int asta;
    private LocalDate data;
    private LocalTime ora;
    private BigDecimal importo;
    private boolean automatica;

    //importo di controfferta
    private BigDecimal importoControfferta;

    //costruttore
    public Offerta(String utenteBase, int asta, LocalDate data, LocalTime ora, BigDecimal importo, boolean automatica, BigDecimal importoControfferta) {
        this.utenteBase = utenteBase;
        this.asta = asta;
        this.data = data;
        this.ora = ora;
        this.importo = importo;
        this.automatica = automatica;
        this.importoControfferta = importoControfferta;
    }

    public Offerta() {
        //costruttore vuoto
    }

    //setters
    public void setUtenteBase(String utenteBase) {
        this.utenteBase = utenteBase;
    }

    public void setAsta(int asta) {
        this.asta = asta;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public void setOra(LocalTime ora) {
        this.ora = ora;
    }

    public void setImporto(BigDecimal importo) {
        this.importo = importo;
    }

    public void setAutomatica(boolean automatica) {
        this.automatica = automatica;
    }

    public void setImportoControfferta(BigDecimal importoControfferta) {
        this.importoControfferta = importoControfferta;
    }

    //getters
    public String getUtenteBase() {
        return utenteBase;
    }

    public int getAsta() {
        return asta;
    }

    public LocalDate getData() {
        return data;
    }

    public LocalTime getOra() {
        return ora;
    }

    public BigDecimal getImporto() {
        return importo;
    }

    public boolean isAutomatica() {
        return automatica;
    }

    public BigDecimal getImportoControfferta() {
        return importoControfferta;
    }

    @Override
    public String toString() {
        return "Offerta{" +
                "utenteBase='" + this.utenteBase + '\'' +
                ", asta=" + this.asta +
                ", data=" + this.data +
                ", ora=" + this.ora +
                ", importo=" + this.importo +
                ", automatica=" + this.automatica +
                ", importoControfferta=" + this.importoControfferta +
                '}';
    }
}
