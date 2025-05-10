package it.uniroma2.asteonline.model.domain;

import java.util.ArrayList;
import java.util.List;

public class Categoria {
    private String nomeCategoria;
    private String categoriaSuperiore;
    private int livello;

    private List<Categoria> figli = new ArrayList<Categoria>();

    public Categoria(String nomeCategoria, int livello, String categoriaSuperiore) {
        this.nomeCategoria = nomeCategoria;
        this.livello = livello;
        this.categoriaSuperiore = categoriaSuperiore;
    }

    public Categoria() {
        //costruttore vuoto
    }


    //setter
    public void setNomeCategoria(String nomeCat) {
        this.nomeCategoria = nomeCat;
    }

    public void setLivello(int livello) {
        this.livello = livello;
    }

    public void setCategoriaSuperiore(String catSup) {
        this.categoriaSuperiore = catSup;
    }

    public void addFiglio(Categoria f) {
        this.figli.add(f);
    }

    //getter
    public String getNomeCategoria() {
        return this.nomeCategoria;
    }

    public int getLivello() {
        return this.livello;
    }

    public String getCategoriaSuperiore() {
        return this.categoriaSuperiore;
    }

    public List<Categoria> getFigli() {
        return this.figli;
    }


}
