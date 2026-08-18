package com.example.sceglicosamangiare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

public class AggiuntaPiattiActivity extends AppCompatActivity {

    private PiattoRepository repo;
    private int editingId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aggiunta_piatti);

        repo = new PiattoRepository(this);

        // navigation
        BackHomeActivity();

        // read editing id if present
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("piatto_id")) {
            editingId = intent.getIntExtra("piatto_id", -1);
        }

        final EditText TVnomeinput = findViewById(R.id.nomeInput);
        final Spinner spinnerPortata = findViewById(R.id.spinnerPortata);
        final Spinner spinnerNutrienti = findViewById(R.id.spinnerNutrienti);
        Button salvaBtn = findViewById(R.id.salvaBtn);

        // if editing load values
        if (editingId >= 0) {
            Piatto p = repo.getById(editingId);
            if (p != null) {
                if (TVnomeinput != null) TVnomeinput.setText(p.getNomePiatto());
                // spinners: best-effort selection by matching string
                if (spinnerPortata != null && p.getPortata() != null) {
                    for (int i = 0; i < spinnerPortata.getCount(); i++) {
                        Object it = spinnerPortata.getItemAtPosition(i);
                        if (it != null && it.toString().equalsIgnoreCase(p.getPortata())) {
                            spinnerPortata.setSelection(i);
                            break;
                        }
                    }
                }
                if (spinnerNutrienti != null && p.getNutrienti() != null) {
                    for (int i = 0; i < spinnerNutrienti.getCount(); i++) {
                        Object it = spinnerNutrienti.getItemAtPosition(i);
                        if (it != null && it.toString().equalsIgnoreCase(p.getNutrienti())) {
                            spinnerNutrienti.setSelection(i);
                            break;
                        }
                    }
                }
            }
        }

        if (salvaBtn != null) {
            salvaBtn.setOnClickListener(v -> {
                String nomeInput = TVnomeinput != null && TVnomeinput.getText() != null ? TVnomeinput.getText().toString().trim() : "";
                if (nomeInput.isEmpty()) {
                    Toast.makeText(AggiuntaPiattiActivity.this, "Inserisci il nome del piatto", Toast.LENGTH_SHORT).show();
                    return;
                }
                String nomePiattoNew = nomeInput;
                nomePiattoNew = nomePiattoNew.toLowerCase();
                if (!nomePiattoNew.isEmpty()) {
                    nomePiattoNew = nomePiattoNew.substring(0, 1).toUpperCase() + nomePiattoNew.substring(1);
                }

                String spinnerPortataValue = (spinnerPortata != null && spinnerPortata.getSelectedItem() != null) ? spinnerPortata.getSelectedItem().toString() : "";
                if (spinnerPortataValue.equalsIgnoreCase("Che portata è?")) {
                    Toast.makeText(AggiuntaPiattiActivity.this, "inserirsci che tipo di portata è", Toast.LENGTH_SHORT).show();
                    return;
                }

                String spinnerNutrientiValue = (spinnerNutrienti != null && spinnerNutrienti.getSelectedItem() != null) ? spinnerNutrienti.getSelectedItem().toString() : "";
                if (spinnerNutrientiValue.equalsIgnoreCase("Che nutrienti contiene?")) {
                    Toast.makeText(AggiuntaPiattiActivity.this, "inserirsci che nutrienti ci sono", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (editingId >= 0) {
                    if (repo.existsByNameAndPortata(nomePiattoNew, spinnerPortataValue, editingId)) {
                        Toast.makeText(AggiuntaPiattiActivity.this, "Esiste già un piatto con questo nome e portata", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Piatto p = new Piatto(editingId, nomePiattoNew, spinnerPortataValue, spinnerNutrientiValue, Boolean.TRUE);
                    boolean ok = repo.updateOne(p);
                    if (ok) {
                        Toast.makeText(AggiuntaPiattiActivity.this, "Piatto aggiornato", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        Toast.makeText(AggiuntaPiattiActivity.this, "Aggiornamento fallito", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    if (repo.existsByNameAndPortata(nomePiattoNew, spinnerPortataValue, -1)) {
                        Toast.makeText(AggiuntaPiattiActivity.this, "Esiste già un piatto con questo nome e portata", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Piatto piatto = new Piatto(-1, nomePiattoNew, spinnerPortataValue, spinnerNutrientiValue, Boolean.TRUE);
                    boolean success = repo.addOne(piatto);

                    if (success) {
                        Toast.makeText(AggiuntaPiattiActivity.this, "Piatto inserito con successo!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(AggiuntaPiattiActivity.this, "Ops! Qualcosa è andato storto...", Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }

    }

    private void BackHomeActivity() {
        ImageButton homeBtn = findViewById(R.id.homeBtn);
        if (homeBtn != null) {
            homeBtn.setOnClickListener(v -> {
                Intent backHome = new Intent(AggiuntaPiattiActivity.this, MainActivity.class);
                startActivity(backHome);
            });
        }
    }

}
