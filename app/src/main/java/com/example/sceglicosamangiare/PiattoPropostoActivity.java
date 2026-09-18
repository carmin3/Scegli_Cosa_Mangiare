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
    private String proteinaScelta;
    private Spinner proteinaSpinner;
    private final List<String> opzioniProteina = Arrays.asList("Dieta Bilanciata", "Carne Bianca", "Pesce", "Carne Rossa", "Proteine Vegetali", "Neutro");

    private Piatto currentPrimo;
    private Piatto currentSecondo;
    private Piatto currentContorno;
    private Piatto currentPiattoUnico;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_piatto_proposto);

        repo = new PiattoRepository(PiattoPropostoActivity.this);

        setupSpinner();
        
        // Primo avvio: scelta casuale completa
        sceltaCasualeProteina();
        proteinaSpinner.setSelection(0); // Default a "Dieta Bilanciata"
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
            if (selected.equalsIgnoreCase("Dieta Bilanciata")) {
                sceltaCasualeProteina();
            } else {
                proteinaScelta = selected;
            }
            refreshAllDishes(proteinaScelta);
            
            // Dopo il clic, lo spinner torna a "Dieta Bilanciata"
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
        if (proteina.equalsIgnoreCase("Dieta Bilanciata")) {
            proteina = proteinaScelta;
        }

        Pasto pastoNuovo = repo.generaPasto(proteina);
        Piatto nuovoPiatto = null;
        if (portata.equalsIgnoreCase("Primo")) nuovoPiatto = pastoNuovo.getPrimo();
        else if (portata.equalsIgnoreCase("Secondo")) nuovoPiatto = pastoNuovo.getSecondo();
        else if (portata.equalsIgnoreCase("Contorno")) nuovoPiatto = pastoNuovo.getContorno();
        else if (portata.equalsIgnoreCase("Piatto Unico")) nuovoPiatto = pastoNuovo.getPiattoUnico();

        updateDishUI(viewId, nuovoPiatto);
    }

    private void refreshAllDishes(String proteina) {
        TextView infoTv = findViewById(R.id.infoNutrienteTV);
        if (infoTv != null) {
            if (proteina.equalsIgnoreCase("Proteine Vegetali")) {
                infoTv.setText("Il menù è a base di proteine vegetali");
            } else if (proteina.equalsIgnoreCase("Neutro")) {
                infoTv.setText("Il menù è neutro");
            } else {
                infoTv.setText("Il menù è a base di " + proteina.toLowerCase());
            }
        }

        Pasto pasto = repo.generaPasto(proteina);

        updateDishUI(R.id.primoPropostoTV, pasto.getPrimo());
        updateDishUI(R.id.secondoPropostoTV, pasto.getSecondo());
        updateDishUI(R.id.piattoUnicoPropostoTV, pasto.getPiattoUnico());
        updateDishUI(R.id.contornoPropostoTV, pasto.getContorno());
    }

    private void sceltaCasualeProteina() {
        //Scelta del tipo di proteina per il piatto casuale
        WeightManager weightManager = new WeightManager(this);
        SceltaCasualeTipoProteina<String> itemDrops = new SceltaCasualeTipoProteina<>();

        itemDrops.addEntry("Carne Rossa", weightManager.getWeight(WeightManager.KEY_CARNE_ROSSA));
        itemDrops.addEntry("Carne Bianca", weightManager.getWeight(WeightManager.KEY_CARNE_BIANCA));
        itemDrops.addEntry("Pesce", weightManager.getWeight(WeightManager.KEY_PESCE));
        itemDrops.addEntry("Proteine Vegetali", weightManager.getWeight(WeightManager.KEY_VEG));
        itemDrops.addEntry("Neutro", weightManager.getWeight(WeightManager.KEY_NEUTRO));
        proteinaScelta = itemDrops.getProteina();
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
