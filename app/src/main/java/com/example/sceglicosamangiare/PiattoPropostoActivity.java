package com.example.sceglicosamangiare;

import androidx.core.content.ContextCompat;
import android.graphics.Color;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.content.Intent;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageButton;

public class PiattoPropostoActivity extends AppCompatActivity {

    private PiattoRepository repo;
    private ArrayList<Piatto> listaPiatti;
    private String proteinaScelta;
    private Spinner proteinaSpinner;
    private final List<String> opzioniProteina = Arrays.asList("Carne Bianca", "Pesce", "Carne Rossa", "Altro");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_piatto_proposto);

        repo = new PiattoRepository(PiattoPropostoActivity.this);
        listaPiatti = repo.getAllData();

        setupSpinner();
        
        // Primo avvio: scelta casuale completa
        sceltaCasualeProteina();
        updateSpinnerSelection();
        refreshAllDishes(proteinaScelta);

        backHomeActivity();
        setupButtons();
    }

    private void setupSpinner() {
        proteinaSpinner = findViewById(R.id.proteinaSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, opzioniProteina);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        proteinaSpinner.setAdapter(adapter);

        proteinaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                findViewById(R.id.btnRicreaMenu).setVisibility(View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void updateSpinnerSelection() {
        int index = opzioniProteina.indexOf(proteinaScelta);
        if (index != -1) {
            proteinaSpinner.setSelection(index);
        }
    }

    private void setupButtons() {
        // Bottone Refresh in alto a destra: torna al comportamento originale (tutto casuale)
        ImageButton refreshBtn = findViewById(R.id.refreshBtn);
        if (refreshBtn != null) {
            refreshBtn.setOnClickListener(v -> {
                sceltaCasualeProteina();
                updateSpinnerSelection();
                refreshAllDishes(proteinaScelta);
                refreshBtn.setBackground(null);
            });
        }

        // Bottone "Ricrea il menù" sotto lo spinner
        findViewById(R.id.btnRicreaMenu).setOnClickListener(v -> {
            proteinaScelta = proteinaSpinner.getSelectedItem().toString();
            refreshAllDishes(proteinaScelta);
        });

        // Bottoni refresh singoli
        findViewById(R.id.refreshPrimoBtn).setOnClickListener(v -> refreshSingleDish(R.id.primoPropostoTV, "Primo"));
        findViewById(R.id.refreshSecondoBtn).setOnClickListener(v -> refreshSingleDish(R.id.secondoPropostoTV, "Secondo"));
        findViewById(R.id.refreshContornoBtn).setOnClickListener(v -> refreshSingleDish(R.id.contornoPropostoTV, "Contorno"));
        findViewById(R.id.refreshPiattoUnicoBtn).setOnClickListener(v -> refreshSingleDish(R.id.piattoUnicoPropostoTV, "Piatto Unico"));
    }

    private void refreshSingleDish(int viewId, String portata) {
        String proteina = proteinaSpinner.getSelectedItem().toString();
        ArrayList<Piatto> piattiFiltrati = filtraPerProteina(proteina);
        Piatto nuovoPiatto = pickRandomByPortata(piattiFiltrati, portata);
        updateDishUI(viewId, nuovoPiatto);
    }

    private ArrayList<Piatto> filtraPerProteina(String proteina) {
        ArrayList<Piatto> filtrati = new ArrayList<>();
        String proteinaLower = proteina.toLowerCase();
        if (listaPiatti != null) {
            for (Piatto p : listaPiatti) {
                if (p != null && p.getNutrienti() != null && p.getNutrienti().toLowerCase().contains(proteinaLower)) {
                    filtrati.add(p);
                }
            }
        }
        return filtrati;
    }

    private void refreshAllDishes(String proteina) {
        TextView proteinaSceltaTV = findViewById(R.id.proteinaSceltaTV);
        if (proteinaSceltaTV != null) {
            proteinaSceltaTV.setText(getString(R.string.label_proteina_scelta, proteina));
        }

        ArrayList<Piatto> piattiConProteina = filtraPerProteina(proteina);

        updateDishUI(R.id.primoPropostoTV, pickRandomByPortata(piattiConProteina, "Primo"));
        updateDishUI(R.id.secondoPropostoTV, pickRandomByPortata(piattiConProteina, "Secondo"));
        updateDishUI(R.id.piattoUnicoPropostoTV, pickRandomByPortata(piattiConProteina, "Piatto Unico"));
        updateDishUI(R.id.contornoPropostoTV, pickRandomByPortata(listaPiatti, "Contorno"));
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
