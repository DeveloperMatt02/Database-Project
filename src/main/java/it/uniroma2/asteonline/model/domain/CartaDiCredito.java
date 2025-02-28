package it.uniroma2.asteonline.model.domain;

import java.util.Date;

public class CartaDiCredito {
    private String numeroCarta;
    private Integer CVV;
    private Date dataScadenza;

    public CartaDiCredito() {}

    public void setNumeroCarta(String numCarta) {
        this.numeroCarta = numCarta;
    }

    public String getNumeroCarta() {
        return this.numeroCarta;
    }

    public void setCVV(Integer CVV) {
        this.CVV = CVV;
    }

    public Integer getCVV() {
        return this.CVV;
    }

    public void setDataScadenza(Date dataScadenza) {
        this.dataScadenza = dataScadenza;
    }

    public Date getDataScadenza() {
        return this.dataScadenza;
    }
}
