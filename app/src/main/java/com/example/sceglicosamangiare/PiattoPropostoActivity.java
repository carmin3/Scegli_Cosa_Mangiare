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
    private final List<String> opzioniProteina = Arrays.asList("Dieta Equilibrata", "Carne Bianca", "Pesce", "Carne Rossa", "Vegetariano");

    private Piatto currentPrimo;
    private Piatto currentSecondo;
    private Piatto currentContorno;
    private Piatto currentPiattoUnico;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_piatto_proposto);

        repo = new PiattoRepository(PiattoPropostoActivity.this);
        listaPiatti = repo.getAllData();

        setupSpinner();
        
        // Primo avvio: scelta casuale completa
        sceltaCasualeProteina();
        proteinaSpinner.setSelection(0); // Default a "Dieta Equilibrata"
        refreshAllDishes(proteinaScelta);

        backHomeActivity();
        setupButtons();
    }

    private void setupSpinner() {
        proteinaSpinner = findViewById(R.id.proteinaSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, opzioniProteina);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        proteinaSpinner.setAdapter(adapter);
    }

    private void setupButtons() {
        // Bottone "Ricrea il menù" sotto lo spinner
        findViewById(R.id.btnRicreaMenu).setOnClickListener(v -> {
            String selected = proteinaSpinner.getSelectedItem().toString();
            if (selected.equalsIgnoreCase("Dieta Equilibrata")) {
                sceltaCasualeProteina();
            } else {
                proteinaScelta = selected;
            }
            refreshAllDishes(proteinaScelta);
            
            // Dopo il clic, lo spinner torna a "Dieta Equilibrata"
            proteinaSpinner.setSelection(0);
        });

        // Bottoni refresh singoli
        findViewById(R.id.refreshPrimoBtn).setOnClickListener(v -> refreshSingleDish(R.id.primoPropostoTV, "Primo"));
        findViewById(R.id.refreshSecondoBtn).setOnClickListener(v -> refreshSingleDish(R.id.secondoPropostoTV, "Secondo"));
        findViewById(R.id.refreshContornoBtn).setOnClickListener(v -> refreshSingleDish(R.id.contornoPropostoTV, "Contorno"));
        findViewById(R.id.refreshPiattoUnicoBtn).setOnClickListener(v -> refreshSingleDish(R.id.piattoUnicoPropostoTV, "Piatto Unico"));

        // Click listeners per i nomi dei piatti
        findViewById(R.id.primoPropostoTV).setOnClickListener(v -> openDetail(currentPrimo));
        findViewById(R.id.secondoPropostoTV).setOnClickListener(v -> openDetail(currentSecondo));
        findViewById(R.id.contornoPropostoTV).setOnClickListener(v -> openDetail(currentContorno));
        findViewById(R.id.piattoUnicoPropostoTV).setOnClickListener(v -> openDetail(currentPiattoUnico));
    }

    private void refreshSingleDish(int viewId, String portata) {
        String proteina = proteinaSpinner.getSelectedItem().toString();
        if (proteina.equalsIgnoreCase("Dieta Equilibrata")) {
            proteina = proteinaScelta;
        }
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
        TextView infoTv = findViewById(R.id.infoNutrienteTV);
        if (infoTv != null) {
            if (proteina.equalsIgnoreCase("Vegetariano")) {
                infoTv.setText("Il menù è vegetariano");
            } else {
                infoTv.setText("Il menù è a base di " + proteina.toLowerCase());
            }
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

        itemDrops.addEntry("Carne Rossa",  1.0);
        itemDrops.addEntry("Carne Bianca",   2.0);
        itemDrops.addEntry("Pesce",  3.0);
        itemDrops.addEntry("Vegetariano",   4.0);
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
            if (viewId == R.id.primoPropostoTV) currentPrimo = piatto;
            else if (viewId == R.id.secondoPropostoTV) currentSecondo = piatto;
            else if (viewId == R.id.contornoPropostoTV) currentContorno = piatto;
            else if (viewId == R.id.piattoUnicoPropostoTV) currentPiattoUnico = piatto;

            if (piatto != null) {
                tv.setText(piatto.getNomePiatto());
                tv.setAlpha(1.0f);
                tv.setClickable(true);
            } else {
                tv.setText(R.string.nessun_piatto_per_proteina);
                tv.setAlpha(0.5f);
                tv.setClickable(false);
            }
        }
    }

    private void openDetail(Piatto piatto) {
        if (piatto != null) {
            Intent intent = new Intent(this, PiattoDetailActivity.class);
            intent.putExtra(PiattoDetailActivity.EXTRA_PIATTO_ID, piatto.getId());
            startActivity(intent);
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
