package it.uniroma2.asteonline.model.domain;

public class Categoria {
    private String nomeCategoria;
    private String categoriaSuperiore;
    private int livello;

    public Categoria(String nomeCategoria, String categoriaSuperiore,int livello) {
        this.nomeCategoria = nomeCategoria;
        this.categoriaSuperiore = categoriaSuperiore;
        this.livello = livello;
    }

    public Categoria() {}

    public String getNomeCategoria() {
        return nomeCategoria;
    }
    public void setNomeCategoria(String nomeCat) {
        this.nomeCategoria = nomeCat;
    }

    public String getCategoriaSuperiore() {
        return categoriaSuperiore;
    }
    public void setCategoriaSuperiore(String catSup) {
        this.categoriaSuperiore = catSup;
    }

    public int getLivello() { return this.livello; }
    public void setLivello(int livello) { this.livello = livello; }

}
