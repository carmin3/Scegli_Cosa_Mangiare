package com.example.sceglicosamangiare;

public class Pasto {
    private String nutriente;
    private Piatto primo;
    private Piatto secondo;
    private Piatto contorno;
    private Piatto piattoUnico;

    public Pasto(String nutriente) {
        this.nutriente = nutriente;
    }

    public String getNutriente() { return nutriente; }
    public void setNutriente(String nutriente) { this.nutriente = nutriente; }

    public Piatto getPrimo() { return primo; }
    public void setPrimo(Piatto primo) { this.primo = primo; }

    public Piatto getSecondo() { return secondo; }
    public void setSecondo(Piatto secondo) { this.secondo = secondo; }

    public Piatto getContorno() { return contorno; }
    public void setContorno(Piatto contorno) { this.contorno = contorno; }

    public Piatto getPiattoUnico() { return piattoUnico; }
    public void setPiattoUnico(Piatto piattoUnico) { this.piattoUnico = piattoUnico; }
    
    public boolean isPiattoUnico() {
        return piattoUnico != null;
    }
}
