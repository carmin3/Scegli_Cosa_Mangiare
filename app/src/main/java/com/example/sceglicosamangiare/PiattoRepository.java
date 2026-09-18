package com.example.sceglicosamangiare;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

public class PiattoRepository {
    private final DataBaseHelper personal;
    private final BaseDbHelper base;

    public PiattoRepository(Context ctx) {
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
        Pasto pasto = new Pasto(nutrienteScelto);
        ArrayList<Piatto> allData = getAllData();

        // 1. Piatto Unico basato sul nutriente
        ArrayList<Piatto> piattiUnici = new ArrayList<>();
        for (Piatto p : allData) {
            if (p.getPortata().equalsIgnoreCase("Piatto Unico") &&
                    p.getNutrienti() != null && p.getNutrienti().equalsIgnoreCase(nutrienteScelto)) {
                piattiUnici.add(p);
            }
        }
        if (!piattiUnici.isEmpty()) {
            Piatto pu = piattiUnici.get(new Random().nextInt(piattiUnici.size()));
            pasto.setPiattoUnico(pu);
            Log.d("GenerazionePasto", "Estratto Piatto Unico: " + pu.getNomePiatto());
        }

        // 2. Combo Primo + Secondo (almeno uno dei due basato sul nutriente scelto)
        ArrayList<Piatto> piattiComboVincolo = new ArrayList<>();
        for (Piatto p : allData) {
            if ((p.getPortata().equalsIgnoreCase("Primo") || p.getPortata().equalsIgnoreCase("Secondo")) &&
                    p.getNutrienti() != null && p.getNutrienti().equalsIgnoreCase(nutrienteScelto)) {
                piattiComboVincolo.add(p);
            }
        }

        if (!piattiComboVincolo.isEmpty()) {
            Piatto vincolo = piattiComboVincolo.get(new Random().nextInt(piattiComboVincolo.size()));
            Log.d("GenerazionePasto", "Piatto Vincolo estratto: " + vincolo.getNomePiatto() + " (" + vincolo.getPortata() + ", " + vincolo.getDominanzaNutrizionale() + ", " + vincolo.getProfiloGustativo() + ")");
            if (vincolo.getPortata().equalsIgnoreCase("Primo")) {
                pasto.setPrimo(vincolo);
                pasto.setSecondo(pickCompatibleMissing(allData, "Secondo", vincolo));
            } else {
                pasto.setSecondo(vincolo);
                pasto.setPrimo(pickCompatibleMissing(allData, "Primo", vincolo));
            }
            
            if (pasto.getPrimo() != null && pasto.getSecondo() != null) {
                Log.d("GenerazionePasto", "Combo completa: " + pasto.getPrimo().getNomePiatto() + " + " + pasto.getSecondo().getNomePiatto());
            } else {
                Log.w("GenerazionePasto", "Impossibile completare combo per vincolo: " + vincolo.getNomePiatto());
            }

            // 3. Contorno (compatibile con il profilo gustativo della combo)
            pasto.setContorno(pickCompatibleContorno(allData, vincolo.getProfiloGustativo()));
            if (pasto.getContorno() != null) {
                Log.d("GenerazionePasto", "Contorno aggiunto: " + pasto.getContorno().getNomePiatto());
            }
        }

        return pasto;
    }

    public Piatto pickCompatibleMissing(ArrayList<Piatto> all, String portataMancante, Piatto vincolo) {
        ArrayList<Piatto> filtrati = new ArrayList<>();
        String dominanzaCercata = "";
        if (vincolo.getDominanzaNutrizionale() != null) {
            if (vincolo.getDominanzaNutrizionale().equalsIgnoreCase("Carbo-Puro")) dominanzaCercata = "Proteina-Pura";
            else if (vincolo.getDominanzaNutrizionale().equalsIgnoreCase("Proteina-Pura")) dominanzaCercata = "Carbo-Puro";
        }

        for (Piatto p : all) {
            if (p.getPortata().equalsIgnoreCase(portataMancante)) {
                // Filtro Nutrizionale
                if (!dominanzaCercata.isEmpty() && (p.getDominanzaNutrizionale() == null || !p.getDominanzaNutrizionale().equalsIgnoreCase(dominanzaCercata))) continue;

                // Filtro Gustativo
                if (isGustoIncompatibile(vincolo.getProfiloGustativo(), p.getProfiloGustativo())) continue;

                filtrati.add(p);
            }
        }
        if (filtrati.isEmpty()) return null;
        return filtrati.get(new Random().nextInt(filtrati.size()));
    }

    private Piatto pickCompatibleContorno(ArrayList<Piatto> all, String profiloVincolo) {
        ArrayList<Piatto> filtrati = new ArrayList<>();
        for (Piatto p : all) {
            if (p.getPortata().equalsIgnoreCase("Contorno")) {
                if (isGustoIncompatibile(profiloVincolo, p.getProfiloGustativo())) continue;
                filtrati.add(p);
            }
        }
        if (filtrati.isEmpty()) return null;
        return filtrati.get(new Random().nextInt(filtrati.size()));
    }

    private boolean isGustoIncompatibile(String g1, String g2) {
        if (g1 == null || g2 == null || g1.isEmpty() || g2.isEmpty()) return false;
        if ((g1.equalsIgnoreCase("Terra-Forte") && g2.equalsIgnoreCase("Mare")) ||
                (g1.equalsIgnoreCase("Mare") && g2.equalsIgnoreCase("Terra-Forte"))) {
            return true;
        }
        return false;
    }
}
