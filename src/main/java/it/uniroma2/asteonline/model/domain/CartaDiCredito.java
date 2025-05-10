package it.uniroma2.asteonline.model.domain;

import java.time.LocalDate;

public class CartaDiCredito {
    private String numeroCarta;
    private String CVV;
    private LocalDate dataScadenza;

    public CartaDiCredito() {}

    public void setNumeroCarta(String numCarta) {
        this.numeroCarta = numCarta;
    }

    public String getNumeroCarta() {
        return this.numeroCarta;
    }

    public void setCVV(String CVV) {
        this.CVV = CVV;
    }

    public String getCVV() {
        return this.CVV;
    }

    public void setDataScadenza(LocalDate dataScadenza) {
        this.dataScadenza = dataScadenza;
    }

    public LocalDate getDataScadenza() {
        return this.dataScadenza;
    }

    public String toFormat() {
        return numeroCarta + ":" + CVV + ":" + dataScadenza.toString();
    }
}
