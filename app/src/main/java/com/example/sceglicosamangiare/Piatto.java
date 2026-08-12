package com.example.sceglicosamangiare;

public class Piatto {

    private int id;
    private String nomePiatto;
    private String portata;
    private String nutrienti;
    private Boolean personale;
    private Boolean favorito = false;

    // existing constructor (kept for compatibility)
    public Piatto(int id, String nomePiatto, String portata, String nutrienti, Boolean personale) {
        this.id = id;
        this.nomePiatto = nomePiatto;
        this.portata = portata;
        this.nutrienti = nutrienti;
        this.personale = personale;
        this.favorito = false;
    }

    // new constructor with favorito
    public Piatto(int id, String nomePiatto, String portata, String nutrienti, Boolean personale, Boolean favorito) {
        this.id = id;
        this.nomePiatto = nomePiatto;
        this.portata = portata;
        this.nutrienti = nutrienti;
        this.personale = personale;
        this.favorito = favorito != null ? favorito : false;
    }

    @Override
    public String toString() {
        return "Piatto{" +
                "id=" + id +
                ", nomePiatto='" + nomePiatto + '\'' +
                ", portata='" + portata + '\'' +
                ", nutrienti='" + nutrienti + '\'' +
                ", personale=" + personale +
                ", favorito=" + favorito +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNomePiatto() {
        return nomePiatto;
    }

    public void setNomePiatto(String nomePiatto) {
        this.nomePiatto = nomePiatto;
    }

    public String getPortata() {
        return portata;
    }

    public void setPortata(String portata) {
        this.portata = portata;
    }

    public String getNutrienti() {
        return nutrienti;
    }

    public void setNutrienti(String nutrienti) {
        this.nutrienti = nutrienti;
    }

    public Boolean getPersonale() {
        return personale;
    }

    public void setPersonale(Boolean personale) {
        this.personale = personale;
    }

    public Boolean getFavorito() {
        return favorito;
    }

    public void setFavorito(Boolean favorito) {
        this.favorito = favorito != null ? favorito : false;
    }
}
