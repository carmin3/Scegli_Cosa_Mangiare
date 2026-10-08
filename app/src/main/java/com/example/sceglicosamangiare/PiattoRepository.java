package com.example.sceglicosamangiare;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class PiattoRepository {
    private final DataBaseHelper personal;
    private final BaseDbHelper base;
    private final Context context;

    public PiattoRepository(Context ctx) {
        this.context = ctx;
        personal = new DataBaseHelper(ctx);
        base = new BaseDbHelper(ctx);
    }

    // merged view
    public ArrayList<Piatto> getAllData() {
        ArrayList<Piatto> result = new ArrayList<>();
        ArrayList<Piatto> personalList = personal.getAllPersonalNonTombstone();
        // map base ids overridden
        java.util.Set<Integer> overriddenBaseIds = new java.util.HashSet<>();
        for (Piatto p: personalList) {
            if (p.getBaseId() != null) overriddenBaseIds.add(p.getBaseId());
            // add personal items (including those that override base)
            result.add(p);
        }
        // add base items not overridden
        ArrayList<Piatto> baseList = base.getAllBasePiatti();
        for (Piatto b: baseList) {
            if (!overriddenBaseIds.contains(b.getId())) result.add(b);
        }
        return result;
    }

    public Piatto getById(int id) {
        // try personal by id
        Piatto p = personal.getPersonalById(id);
        if (p != null) return p;
        // try personal by base id
        Piatto p2 = personal.getPersonalByBaseId(id);
        if (p2 != null && !p2.getTombstone()) return p2;
        if (p2 != null && p2.getTombstone()) return null;
        // fallback to base
        return base.getBaseById(id);
    }

    public boolean addOne(Piatto p) {
        long id = personal.insertPersonal(p, null);
        return id != -1;
    }

    public boolean updateOne(Piatto p) {
        // if exists personal by id -> update
        if (personal.getPersonalById(p.getId()) != null) return personal.updatePersonalById(p.getId(), p);
        // else if editing a base item: create personal override with base_id = p.id
        int baseId = p.getId();
        long newId = personal.insertPersonal(p, baseId);
        return newId != -1;
    }

    public boolean deleteOne(int id) {
        // if personal has direct id -> delete
        if (personal.getPersonalById(id) != null) return personal.deletePersonalById(id);
        // else mark tombstone for base id
        return personal.markTombstoneForBase(id);
    }

    public boolean setFavorite(int id, boolean value) {
        // if personal has id -> update
        Piatto p = personal.getPersonalById(id);
        if (p != null) {
            if (p.getBaseId() != null && (p.getNomePiatto() == null || p.getNomePiatto().isEmpty())) {
                Piatto bp = base.getBaseById(p.getBaseId());
                if (bp != null) {
                    p.setNomePiatto(bp.getNomePiatto());
                    p.setPortata(bp.getPortata());
                    p.setNutrienti(bp.getNutrienti());
                    personal.updatePersonalById(p.getId(), p);
                }
            }
            return personal.setFavoritePersonal(id, value);
        }
        // else if exists personal override by base id
        Piatto p2 = personal.getPersonalByBaseId(id);
        if (p2 != null) {
            if (p2.getNomePiatto() == null || p2.getNomePiatto().isEmpty()) {
                Piatto bp = base.getBaseById(id);
                if (bp != null) {
                    p2.setNomePiatto(bp.getNomePiatto());
                    p2.setPortata(bp.getPortata());
                    p2.setNutrienti(bp.getNutrienti());
                    personal.updatePersonalById(p2.getId(), p2);
                }
            }
            return personal.setFavoritePersonal(p2.getId(), value);
        }
        // else create override with favorite
        Piatto baseP = base.getBaseById(id);
        if (baseP == null) return false;
        Piatto newP = new Piatto(-1, baseP.getNomePiatto(), baseP.getPortata(), baseP.getNutrienti(), baseP.getDominanzaNutrizionale(), baseP.getProfiloGustativo(), true, value, id, false);
        long nid = personal.insertPersonal(newP, id);
        return nid != -1;
    }

    public boolean existsByNameAndPortata(String nome, String portata, int excludeId) {
        String n = nome != null ? nome.toLowerCase().trim() : "";
        String p = portata != null ? portata.toLowerCase().trim() : "";
        ArrayList<Piatto> all = getAllData();
        for (Piatto item: all) {
            if (item == null) continue;
            if (item.getNomePiatto() == null || item.getPortata() == null) continue;
            if (item.getNomePiatto().toLowerCase().trim().equals(n) && item.getPortata().toLowerCase().trim().equals(p)) {
                if (excludeId >= 0 && item.getId() == excludeId) continue;
                return true;
            }
        }
        return false;
    }

    public ArrayList<Piatto> searchByName(String query) {
        String q = query.toLowerCase().trim();
        ArrayList<Piatto> all = getAllData();
        ArrayList<Piatto> filtered = new ArrayList<>();
        for (Piatto p : all) {
            if (p.getNomePiatto().toLowerCase().contains(q)) {
                filtered.add(p);
            }
        }
        return filtered;
    }

    public Piatto pickRandomByPortataAndProteina(String portata, String proteina) {
        ArrayList<Piatto> all = getAllData();
        ArrayList<Piatto> filtered = new ArrayList<>();
        String protLower = proteina.toLowerCase().trim();
        boolean filterProt = !protLower.equals("casuale") && !protLower.equals("dieta bilanciata");

        for (Piatto p : all) {
            if (p.getPortata() != null && p.getPortata().equalsIgnoreCase(portata)) {
                if (!filterProt || (p.getNutrienti() != null && p.getNutrienti().toLowerCase().contains(protLower))) {
                    filtered.add(p);
                }
            }
        }

        if (filtered.isEmpty()) return null;
        return filtered.get(new Random().nextInt(filtered.size()));
    }

    public Pasto generaPasto(String nutrienteScelto) {
        Log.d("GenerazionePasto", "Inizio generazione per nutriente: " + nutrienteScelto);
        WeightManager weightManager = new WeightManager(context);
        boolean soloPreferiti = weightManager.isSoloPreferiti();
        ArrayList<Piatto> allData = getAllData();

        // Filtra per preferiti se richiesto
        ArrayList<Piatto> pool = new ArrayList<>();
        for (Piatto p : allData) {
            if (p == null) continue;
            if (soloPreferiti && (p.getFavorito() == null || !p.getFavorito())) {
                continue;
            }
            pool.add(p);
        }

        int tentativi = 1;
        Random random = new Random();

        while (tentativi <= 10) {
            // STEP 1: VERIFICA TENTATIVI E SELEZIONE BASE
            String baseDelPiatto = "";
            if (tentativi == 1 && nutrienteScelto != null && 
                (nutrienteScelto.equalsIgnoreCase("Carne Bianca") ||
                 nutrienteScelto.equalsIgnoreCase("Carne Rossa") ||
                 nutrienteScelto.equalsIgnoreCase("Pesce") ||
                 nutrienteScelto.equalsIgnoreCase("Vegetariano"))) {
                baseDelPiatto = nutrienteScelto;
            } else {
                SceltaCasualeTipoProteina<String> itemDrops = new SceltaCasualeTipoProteina<>();
                itemDrops.addEntry("Carne Rossa", weightManager.getWeight(WeightManager.KEY_CARNE_ROSSA));
                itemDrops.addEntry("Carne Bianca", weightManager.getWeight(WeightManager.KEY_CARNE_BIANCA));
                itemDrops.addEntry("Pesce", weightManager.getWeight(WeightManager.KEY_PESCE));
                itemDrops.addEntry("Vegetariano", weightManager.getWeight(WeightManager.KEY_VEG));
                baseDelPiatto = itemDrops.getProteina();
                if (baseDelPiatto == null) baseDelPiatto = "Vegetariano";
            }
            Log.d("GenerazionePasto", "Tentativo " + tentativi + ": Base del Piatto = " + baseDelPiatto);

            // STEP 2: DEFINIZIONE DOMINIO PER CIASCUNA PORTATA
            ArrayList<Piatto> gruppoPrimi = new ArrayList<>();
            ArrayList<Piatto> gruppoSecondi = new ArrayList<>();
            ArrayList<Piatto> gruppoContorni = new ArrayList<>();

            for (Piatto p : pool) {
                if (p.getPortata() == null) continue;
                String port = p.getPortata().trim();
                String nut = p.getNutrienti() != null ? p.getNutrienti().trim() : "";

                boolean matchesBase = false;
                if (baseDelPiatto.equalsIgnoreCase("Vegetariano")) {
                    matchesBase = nut.equalsIgnoreCase("Vegetariano");
                } else {
                    matchesBase = nut.equalsIgnoreCase(baseDelPiatto) || nut.equalsIgnoreCase("Vegetariano");
                }

                if (matchesBase) {
                    if (port.equalsIgnoreCase("Primo")) {
                        gruppoPrimi.add(p);
                    } else if (port.equalsIgnoreCase("Secondo")) {
                        gruppoSecondi.add(p);
                    } else if (port.equalsIgnoreCase("Contorno")) {
                        gruppoContorni.add(p);
                    }
                }
            }

            // STEP 3: DEFINIZIONE SEQUENZA DI ESTRAZIONE
            List<String> portateList = new ArrayList<>(Arrays.asList("Primo", "Secondo", "Contorno"));
            java.util.Collections.shuffle(portateList, random);
            String portata1 = portateList.get(0);
            String portata2 = portateList.get(1);
            String portata3 = portateList.get(2);

            ArrayList<Piatto> gruppo1 = getGroupForPortata(portata1, gruppoPrimi, gruppoSecondi, gruppoContorni);
            ArrayList<Piatto> gruppo2 = getGroupForPortata(portata2, gruppoPrimi, gruppoSecondi, gruppoContorni);
            ArrayList<Piatto> gruppo3 = getGroupForPortata(portata3, gruppoPrimi, gruppoSecondi, gruppoContorni);

            // STEP 4: ESTRAZIONE PORTATA_1
            if (gruppo1.isEmpty()) {
                Log.w("GenerazionePasto", "Gruppo 1 vuoto per " + portata1 + ". Tentativi++");
                tentativi++;
                continue;
            }
            Piatto piatto1 = gruppo1.get(random.nextInt(gruppo1.size()));
            String nutrienti1 = piatto1.getDominanzaNutrizionale();
            String gusto1 = piatto1.getProfiloGustativo();

            // STEP 5: FILTRAGGIO ED ESTRAZIONE PORTATA_2
            ArrayList<Piatto> gruppo2Filtrato = new ArrayList<>();
            for (Piatto p : gruppo2) {
                String g = p.getProfiloGustativo() != null ? p.getProfiloGustativo() : "";
                String n = p.getDominanzaNutrizionale() != null ? p.getDominanzaNutrizionale() : "";

                // Filtro sul Gusto
                boolean gustoOk = false;
                if (gusto1 != null && gusto1.equalsIgnoreCase("Di Terra")) {
                    gustoOk = g.equalsIgnoreCase("Di Terra") || g.equalsIgnoreCase("Vegetale");
                } else if (gusto1 != null && gusto1.equalsIgnoreCase("Di Mare")) {
                    gustoOk = g.equalsIgnoreCase("Di Mare") || g.equalsIgnoreCase("Vegetale");
                } else {
                    gustoOk = true;
                }

                if (!gustoOk) continue;

                // Filtro sui Nutrienti
                boolean nutrientiOk = false;
                if (nutrienti1 != null && nutrienti1.equalsIgnoreCase("Fibre e vitamine")) {
                    nutrientiOk = true;
                } else if (nutrienti1 != null && nutrienti1.equalsIgnoreCase("Carboidrati")) {
                    nutrientiOk = n.equalsIgnoreCase("Proteico") || n.equalsIgnoreCase("Carbo-Proteico") || n.equalsIgnoreCase("Fibre e vitamine");
                } else if (nutrienti1 != null && nutrienti1.equalsIgnoreCase("Proteico")) {
                    nutrientiOk = n.equalsIgnoreCase("Carboidrati") || n.equalsIgnoreCase("Carbo-Proteico") || n.equalsIgnoreCase("Fibre e vitamine");
                } else if (nutrienti1 != null && nutrienti1.equalsIgnoreCase("Carbo-Proteico")) {
                    nutrientiOk = true;
                } else {
                    nutrientiOk = true;
                }

                if (nutrientiOk) {
                    gruppo2Filtrato.add(p);
                }
            }

            if (gruppo2Filtrato.isEmpty()) {
                Log.w("GenerazionePasto", "Gruppo 2 filtrato vuoto per " + portata2 + ". Tentativi++");
                tentativi++;
                continue;
            }

            Piatto piatto2 = gruppo2Filtrato.get(random.nextInt(gruppo2Filtrato.size()));
            String nutrienti2 = piatto2.getDominanzaNutrizionale();
            String gusto2 = piatto2.getProfiloGustativo();

            // STEP 6: FILTRAGGIO ED ESTRAZIONE PORTATA_3
            ArrayList<Piatto> gruppo3Filtrato = new ArrayList<>();
            
            boolean hasTerra = (gusto1 != null && gusto1.equalsIgnoreCase("Di Terra")) || (gusto2 != null && gusto2.equalsIgnoreCase("Di Terra"));
            boolean hasMare = (gusto1 != null && gusto1.equalsIgnoreCase("Di Mare")) || (gusto2 != null && gusto2.equalsIgnoreCase("Di Mare"));
            boolean bothVeg = (gusto1 == null || gusto1.equalsIgnoreCase("Vegetale")) && (gusto2 == null || gusto2.equalsIgnoreCase("Vegetale"));

            boolean hasCarbo = (nutrienti1 != null && (nutrienti1.equalsIgnoreCase("Carboidrati") || nutrienti1.equalsIgnoreCase("Carbo-Proteico"))) ||
                               (nutrienti2 != null && (nutrienti2.equalsIgnoreCase("Carboidrati") || nutrienti2.equalsIgnoreCase("Carbo-Proteico")));
            boolean hasProteico = (nutrienti1 != null && (nutrienti1.equalsIgnoreCase("Proteico") || nutrienti1.equalsIgnoreCase("Carbo-Proteico"))) ||
                                  (nutrienti2 != null && (nutrienti2.equalsIgnoreCase("Proteico") || nutrienti2.equalsIgnoreCase("Carbo-Proteico")));
            boolean hasFibre = (nutrienti1 != null && nutrienti1.equalsIgnoreCase("Fibre e vitamine")) ||
                               (nutrienti2 != null && nutrienti2.equalsIgnoreCase("Fibre e vitamine"));

            for (Piatto p : gruppo3) {
                String g = p.getProfiloGustativo() != null ? p.getProfiloGustativo() : "";
                String n = p.getDominanzaNutrizionale() != null ? p.getDominanzaNutrizionale() : "";

                boolean gusto3Ok = false;
                if (hasTerra) {
                    gusto3Ok = g.equalsIgnoreCase("Di Terra") || g.equalsIgnoreCase("Vegetale");
                } else if (hasMare) {
                    gusto3Ok = g.equalsIgnoreCase("Di Mare") || g.equalsIgnoreCase("Vegetale");
                } else if (bothVeg) {
                    gusto3Ok = true;
                } else {
                    gusto3Ok = true;
                }

                if (!gusto3Ok) continue;

                boolean nutrienti3Ok = false;
                if (!hasCarbo && !hasFibre) {
                    nutrienti3Ok = n.equalsIgnoreCase("Carboidrati") || n.equalsIgnoreCase("Carbo-Proteico");
                } else if (!hasProteico && !hasFibre) {
                    nutrienti3Ok = n.equalsIgnoreCase("Proteico") || n.equalsIgnoreCase("Carbo-Proteico");
                } else if (hasCarbo && hasProteico && !hasFibre) {
                    nutrienti3Ok = n.equalsIgnoreCase("Fibre e vitamine");
                } else if (!hasCarbo && !hasProteico) {
                    nutrienti3Ok = n.equalsIgnoreCase("Carbo-Proteico");
                } else if (hasCarbo && hasProteico && hasFibre) {
                    nutrienti3Ok = true;
                } else {
                    nutrienti3Ok = true;
                }

                if (nutrienti3Ok) {
                    gruppo3Filtrato.add(p);
                }
            }

            if (gruppo3Filtrato.isEmpty()) {
                Log.w("GenerazionePasto", "Gruppo 3 filtrato vuoto per " + portata3 + ". Tentativi++");
                tentativi++;
                continue;
            }

            Piatto piatto3 = gruppo3Filtrato.get(random.nextInt(gruppo3Filtrato.size()));

            Piatto primo = null;
            Piatto secondo = null;
            Piatto contorno = null;

            if (portata1.equals("Primo")) primo = piatto1;
            else if (portata1.equals("Secondo")) secondo = piatto1;
            else if (portata1.equals("Contorno")) contorno = piatto1;

            if (portata2.equals("Primo")) primo = piatto2;
            else if (portata2.equals("Secondo")) secondo = piatto2;
            else if (portata2.equals("Contorno")) contorno = piatto2;

            if (portata3.equals("Primo")) primo = piatto3;
            else if (portata3.equals("Secondo")) secondo = piatto3;
            else if (portata3.equals("Contorno")) contorno = piatto3;

            Pasto pasto = new Pasto(baseDelPiatto);
            pasto.setPrimo(primo);
            pasto.setSecondo(secondo);
            pasto.setContorno(contorno);

            // Piatto Unico
            ArrayList<Piatto> piattiUnici = new ArrayList<>();
            for (Piatto p : pool) {
                if (p.getPortata() != null && p.getPortata().equalsIgnoreCase("Piatto Unico") &&
                    p.getNutrienti() != null && p.getNutrienti().equalsIgnoreCase(baseDelPiatto)) {
                    piattiUnici.add(p);
                }
            }
            if (!piattiUnici.isEmpty()) {
                pasto.setPiattoUnico(piattiUnici.get(random.nextInt(piattiUnici.size())));
            } else {
                for (Piatto p : pool) {
                    if (p.getPortata() != null && p.getPortata().equalsIgnoreCase("Piatto Unico")) {
                        piattiUnici.add(p);
                    }
                }
                if (!piattiUnici.isEmpty()) {
                    pasto.setPiattoUnico(piattiUnici.get(random.nextInt(piattiUnici.size())));
                }
            }

            Log.d("GenerazionePasto", "Menu generato con successo al tentativo " + tentativi);
            return pasto;
        }

        // STEP 7: ESTRAZIONE D'EMERGENZA SENZA VINCOLI (TENTATIVO 11)
        Log.w("GenerazionePasto", "Tentativi esauriti (10 falliti). Esecuzione STEP 7: Estrazione d'emergenza.");

        if (soloPreferiti) {
            weightManager.setSoloPreferiti(false);
            if (context instanceof android.app.Activity) {
                android.app.Activity activity = (android.app.Activity) context;
                if (!activity.isFinishing()) {
                    new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                        try {
                            new androidx.appcompat.app.AlertDialog.Builder(activity)
                                    .setTitle("Attenzione")
                                    .setMessage("Hai davvero pochi piatti preferiti tra cui scegliere! Perché non provi ad aggiungerne qualcun altro? Nel frattempo ti propongo qualcosa di nuovo: magari scoprirai qualche nuova ricetta da aggiungere ai preferiti!")
                                    .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                                    .show();
                        } catch (Exception e) {
                            Log.e("GenerazionePasto", "Errore mostra dialog", e);
                        }
                    });
                }
            }
        }

        Pasto pastoEmergenza = new Pasto(nutrienteScelto != null ? nutrienteScelto : "Dieta Bilanciata");
        
        ArrayList<Piatto> primiAll = new ArrayList<>();
        ArrayList<Piatto> secondiAll = new ArrayList<>();
        ArrayList<Piatto> contorniAll = new ArrayList<>();
        ArrayList<Piatto> uniciAll = new ArrayList<>();

        for (Piatto p : allData) {
            if (p == null || p.getPortata() == null) continue;
            if (p.getPortata().equalsIgnoreCase("Primo")) primiAll.add(p);
            else if (p.getPortata().equalsIgnoreCase("Secondo")) secondiAll.add(p);
            else if (p.getPortata().equalsIgnoreCase("Contorno")) contorniAll.add(p);
            else if (p.getPortata().equalsIgnoreCase("Piatto Unico")) uniciAll.add(p);
        }

        if (!primiAll.isEmpty()) pastoEmergenza.setPrimo(primiAll.get(random.nextInt(primiAll.size())));
        if (!secondiAll.isEmpty()) pastoEmergenza.setSecondo(secondiAll.get(random.nextInt(secondiAll.size())));
        if (!contorniAll.isEmpty()) pastoEmergenza.setContorno(contorniAll.get(random.nextInt(contorniAll.size())));
        if (!uniciAll.isEmpty()) pastoEmergenza.setPiattoUnico(uniciAll.get(random.nextInt(uniciAll.size())));

        return pastoEmergenza;
    }

    private ArrayList<Piatto> getGroupForPortata(String portata, ArrayList<Piatto> primi, ArrayList<Piatto> secondi, ArrayList<Piatto> contorni) {
        if (portata.equalsIgnoreCase("Primo")) return primi;
        if (portata.equalsIgnoreCase("Secondo")) return secondi;
        if (portata.equalsIgnoreCase("Contorno")) return contorni;
        return new ArrayList<>();
    }
}
