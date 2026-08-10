package com.example.sceglicosamangiare;

public class Piatto {

    private int id;
    private String nomePiatto;
    private String portata;
    private String nutrienti;
    private Boolean personale;

    public Piatto(int id, String nomePiatto, String portata, String nutrienti, Boolean personale) {
        this.id = id;
        this.nomePiatto = nomePiatto;
        this.portata = portata;
        this.nutrienti = nutrienti;
        this.personale = personale;
    }

    @Override
    public String toString() {
        return "Piatto{" +
                "id=" + id +
                ", nomePiatto='" + nomePiatto + '\'' +
                ", portata='" + portata + '\'' +
                ", nutrienti='" + nutrienti + '\'' +
                ", personale=" + personale +
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
}
