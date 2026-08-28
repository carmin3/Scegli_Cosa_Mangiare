package com.example.sceglicosamangiare;

public class PianoPasto {
    private String data; // formato yyyy-MM-dd
    private String proteinaPranzo;
    private String pranzoPrimo, pranzoSecondo, pranzoContorno, pranzoPiattoUnico;
    private String proteinaCena;
    private String cenaPrimo, cenaSecondo, cenaContorno, cenaPiattoUnico;

    public PianoPasto(String data) {
        this.data = data;
        this.proteinaPranzo = "Dieta Bilanciata";
        this.proteinaCena = "Dieta Bilanciata";
        this.pranzoPrimo = ""; this.pranzoSecondo = ""; this.pranzoContorno = ""; this.pranzoPiattoUnico = "";
        this.cenaPrimo = ""; this.cenaSecondo = ""; this.cenaContorno = ""; this.cenaPiattoUnico = "";
    }

    public PianoPasto(String data, String proteinaPranzo, String pranzoPrimo, String pranzoSecondo, String pranzoContorno, String pranzoPiattoUnico,
                      String proteinaCena, String cenaPrimo, String cenaSecondo, String cenaContorno, String cenaPiattoUnico) {
        this.data = data;
        this.proteinaPranzo = proteinaPranzo;
        this.pranzoPrimo = pranzoPrimo;
        this.pranzoSecondo = pranzoSecondo;
        this.pranzoContorno = pranzoContorno;
        this.pranzoPiattoUnico = pranzoPiattoUnico;
        this.proteinaCena = proteinaCena;
        this.cenaPrimo = cenaPrimo;
        this.cenaSecondo = cenaSecondo;
        this.cenaContorno = cenaContorno;
        this.cenaPiattoUnico = cenaPiattoUnico;
    }

    public String getData() { return data; }
    public void setData(String data) { this.data = data; }

    public String getProteinaPranzo() { return proteinaPranzo; }
    public void setProteinaPranzo(String proteinaPranzo) { this.proteinaPranzo = proteinaPranzo; }

    public String getPranzoPrimo() { return pranzoPrimo; }
    public void setPranzoPrimo(String pranzoPrimo) { this.pranzoPrimo = pranzoPrimo; }

    public String getPranzoSecondo() { return pranzoSecondo; }
    public void setPranzoSecondo(String pranzoSecondo) { this.pranzoSecondo = pranzoSecondo; }

    public String getPranzoContorno() { return pranzoContorno; }
    public void setPranzoContorno(String pranzoContorno) { this.pranzoContorno = pranzoContorno; }

    public String getPranzoPiattoUnico() { return pranzoPiattoUnico; }
    public void setPranzoPiattoUnico(String pranzoPiattoUnico) { this.pranzoPiattoUnico = pranzoPiattoUnico; }

    public String getProteinaCena() { return proteinaCena; }
    public void setProteinaCena(String proteinaCena) { this.proteinaCena = proteinaCena; }

    public String getCenaPrimo() { return cenaPrimo; }
    public void setCenaPrimo(String cenaPrimo) { this.cenaPrimo = cenaPrimo; }

    public String getCenaSecondo() { return cenaSecondo; }
    public void setCenaSecondo(String cenaSecondo) { this.cenaSecondo = cenaSecondo; }

    public String getCenaContorno() { return cenaContorno; }
    public void setCenaContorno(String cenaContorno) { this.cenaContorno = cenaContorno; }

    public String getCenaPiattoUnico() { return cenaPiattoUnico; }
    public void setCenaPiattoUnico(String cenaPiattoUnico) { this.cenaPiattoUnico = cenaPiattoUnico; }
}
