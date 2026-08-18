package com.example.sceglicosamangiare;

import android.content.Intent;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageButton;

public class PiattoPropostoActivity extends AppCompatActivity {

    private PiattoRepository repo;
    private ArrayList<Piatto> listaPiatti;
    private String proteinaScelta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_piatto_proposto);

        backHomeActivity();
        importaDatabase();
        sceltaCasualeProteina();
        sceltaCasualePiatto();
        refreshActivity();

    }

    private void refreshActivity() {
        ImageButton refreshBtn = findViewById(R.id.refreshBtn);
        if (refreshBtn != null) {
            refreshBtn.setOnClickListener(v -> {
                sceltaCasualeProteina();
                sceltaCasualePiatto();
            });
        }
    }


    public void importaDatabase() {

        listaPiatti = new ArrayList<>();
        repo = new PiattoRepository(PiattoPropostoActivity.this);
        listaPiatti = repo.getAllData();

    }
    private void sceltaCasualeProteina() {
        //Scelta del tipo di proteina per il piatto casuale
        SceltaCasualeTipoProteina<String> itemDrops = new SceltaCasualeTipoProteina<>();

        itemDrops.addEntry("Carne Rossa",  5.0);
        itemDrops.addEntry("Carne Bianca",   20.0);
        itemDrops.addEntry("Pesce",  45.0);
        itemDrops.addEntry("Altro",   20.0);
        proteinaScelta = itemDrops.getProteina();

    }

    private void sceltaCasualePiatto() {
        TextView proteinaSceltaTV = findViewById(R.id.proteinaSceltaTV);
        if (proteinaSceltaTV != null) {
            proteinaSceltaTV.setText(getString(R.string.label_proteina_scelta, proteinaScelta));
        }

        // Filtra piatti per proteina
        ArrayList<Piatto> piattiConProteina = new ArrayList<>();
        if (proteinaScelta == null) proteinaScelta = "";
        String proteinaLower = proteinaScelta.toLowerCase();

        if (listaPiatti != null) {
            for (Piatto p : listaPiatti) {
                if (p != null && p.getNutrienti() != null && p.getNutrienti().toLowerCase().contains(proteinaLower)) {
                    piattiConProteina.add(p);
                }
            }
        }

        // Seleziona Primo, Secondo, Piatto Unico con proteina
        Piatto primo = pickRandomByPortata(piattiConProteina, "Primo");
        Piatto secondo = pickRandomByPortata(piattiConProteina, "Secondo");
        Piatto piattoUnico = pickRandomByPortata(piattiConProteina, "Piatto Unico");

        // Seleziona Contorno senza vincolo proteina
        Piatto contorno = pickRandomByPortata(listaPiatti, "Contorno");

        // Aggiorna UI
        updateDishUI(R.id.primoPropostoTV, primo);
        updateDishUI(R.id.secondoPropostoTV, secondo);
        updateDishUI(R.id.piattoUnicoPropostoTV, piattoUnico);
        updateDishUI(R.id.contornoPropostoTV, contorno);
    }

    private Piatto pickRandomByPortata(ArrayList<Piatto> source, String portata) {
        if (source == null) return null;
        ArrayList<Piatto> filtered = new ArrayList<>();
        for (Piatto p : source) {
            if (p.getPortata() != null && p.getPortata().equalsIgnoreCase(portata)) {
                filtered.add(p);
            }
        }
        if (filtered.isEmpty()) return null;
        return filtered.get(new Random().nextInt(filtered.size()));
    }

    private void updateDishUI(int viewId, Piatto piatto) {
        TextView tv = findViewById(viewId);
        if (tv != null) {
            if (piatto != null) {
                tv.setText(piatto.getNomePiatto());
                tv.setAlpha(1.0f);
            } else {
                tv.setText(R.string.nessun_piatto_per_proteina);
                tv.setAlpha(0.5f);
            }
        }
    }

    private void backHomeActivity() {
        ImageButton homeBtn = findViewById(R.id.homeBtn);
        if (homeBtn != null) {
            homeBtn.setOnClickListener(v -> {
                Intent backHome = new Intent(PiattoPropostoActivity.this, MainActivity.class);
                startActivity(backHome);
            });
        }
    }
}
