package com.example.sceglicosamangiare;

public class Piatto {

    private int id;
    private String nomePiatto;
    private String portata;
    private String nutrienti;
    private Boolean personale;
    private Boolean favorito = false;
    private Integer baseId; // id nel db base se override
    private Boolean tombstone = false; // se true nasconde il piatto base

    // constructor for base or personal with baseId
    public Piatto(int id, String nomePiatto, String portata, String nutrienti, Boolean personale, Boolean favorito, Integer baseId, Boolean tombstone) {
        this.id = id;
        this.nomePiatto = nomePiatto;
        this.portata = portata;
        this.nutrienti = nutrienti;
        this.personale = personale;
        this.favorito = favorito != null ? favorito : false;
        this.baseId = baseId;
        this.tombstone = tombstone != null ? tombstone : false;
    }

    // convenience constructor (kept for compatibility)
    public Piatto(int id, String nomePiatto, String portata, String nutrienti, Boolean personale) {
        this(id, nomePiatto, portata, nutrienti, personale, false, null, false);
    }

    public Piatto(int id, String nomePiatto, String portata, String nutrienti, Boolean personale, Boolean favorito) {
        this(id, nomePiatto, portata, nutrienti, personale, favorito, null, false);
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
                ", baseId=" + baseId +
                ", tombstone=" + tombstone +
                '}';
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNomePiatto() { return nomePiatto; }
    public void setNomePiatto(String nomePiatto) { this.nomePiatto = nomePiatto; }
    public String getPortata() { return portata; }
    public void setPortata(String portata) { this.portata = portata; }
    public String getNutrienti() { return nutrienti; }
    public void setNutrienti(String nutrienti) { this.nutrienti = nutrienti; }
    public Boolean getPersonale() { return personale; }
    public void setPersonale(Boolean personale) { this.personale = personale; }
    public Boolean getFavorito() { return favorito; }
    public void setFavorito(Boolean favorito) { this.favorito = favorito != null ? favorito : false; }
    public Integer getBaseId() { return baseId; }
    public void setBaseId(Integer baseId) { this.baseId = baseId; }
    public Boolean getTombstone() { return tombstone; }
    public void setTombstone(Boolean tombstone) { this.tombstone = tombstone != null ? tombstone : false; }
}
