package com.example.sceglicosamangiare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ListView;
import android.widget.TextView;

public class AggiuntaPiattiActivity extends AppCompatActivity {

    public String nomePiattoNew;
    public String portataNew;
    public String nutrientiNew;
    private int editingId = -1;
    private DataBaseHelper dataBaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aggiunta_piatti);

        dataBaseHelper = new DataBaseHelper(AggiuntaPiattiActivity.this);

        BackHomeActivity();
        ListaPiattiActivity();

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("piatto_id")) {
            editingId = intent.getIntExtra("piatto_id", -1);
        }

        // Title and ListView visibility depending on mode
        TextView titleText = (TextView) findViewById(R.id.titleText);
        ListView lvDb = (ListView) findViewById(R.id.LV_db);
        if (titleText != null) {
            if (editingId >= 0) titleText.setText("Modifica un piatto");
            else titleText.setText("Aggiungi un piatto");
        }
        if (lvDb != null) {
            lvDb.setVisibility(View.VISIBLE);
        }

        final Button aggiungiPiattoBtn = (Button)findViewById(R.id.aggiungiPiattoBtn);
        if (editingId >= 0) {
            // edit mode: pre-populate
            Piatto p = dataBaseHelper.getById(editingId);
            if (p != null) {
                Spinner spinnerPortata = findViewById(R.id.portataSpinner);
                Spinner spinnerNutrienti = findViewById(R.id.nutrientiSpinner);
                EditText TVnomeinput = (EditText) findViewById(R.id.nomeDelPiattoCasualeTV);
                if (TVnomeinput != null) TVnomeinput.setText(p.getNomePiatto());
                // for spinners we assume the values exist; advanced: set selection by value
                aggiungiPiattoBtn.setText("Salva modifica");
            }
        }

        if (aggiungiPiattoBtn != null) {
            aggiungiPiattoBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Spinner spinnerPortata = findViewById(R.id.portataSpinner);
                    Spinner spinnerNutrienti = findViewById(R.id.nutrientiSpinner);
                    EditText TVnomeinput = (EditText) findViewById(R.id.nomeDelPiattoCasualeTV);

                    if (TVnomeinput == null) return;

                    String nomeInput = TVnomeinput.getText() != null ? TVnomeinput.getText().toString().trim() : "";

                    //il primo if controlla il nome del piatto e lo scrive nella variabile nomePiattoNew
                    if (nomeInput.isEmpty()) {
                        Toast.makeText(AggiuntaPiattiActivity.this, "Inserisci il nome del piatto", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    nomePiattoNew = nomeInput;
                    nomePiattoNew = nomePiattoNew.toLowerCase();
                    if (nomePiattoNew.length() >= 1) {
                        nomePiattoNew = nomePiattoNew.substring(0, 1).toUpperCase() + nomePiattoNew.substring(1);
                    }

                    // il secondo if controlla lo spinner della portata e lo scrive nella variabile portataNew
                    String spinnerPortataValue = (spinnerPortata != null && spinnerPortata.getSelectedItem() != null) ? spinnerPortata.getSelectedItem().toString() : "";
                    if (spinnerPortataValue.equalsIgnoreCase("Che portata è?")) {
                        Toast.makeText(AggiuntaPiattiActivity.this, "inserirsci che tipo di portata è", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    portataNew = spinnerPortataValue;

                    // il terzo if controlla lo spinner dei nutrienti e lo riporta nella variabile nutrientiNew
                    String spinnerNutrientiValue = (spinnerNutrienti != null && spinnerNutrienti.getSelectedItem() != null) ? spinnerNutrienti.getSelectedItem().toString() : "";
                    if (spinnerNutrientiValue.equalsIgnoreCase("Che nutrienti contiene?")){
                        Toast.makeText(AggiuntaPiattiActivity.this, "inserirsci che nutrienti ci sono", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    nutrientiNew = spinnerNutrientiValue;

                    if (editingId >= 0) {
                        // edit flow
                        // check duplicates excluding current id
                        if (dataBaseHelper.existsByNameAndPortata(nomePiattoNew, portataNew, editingId)) {
                            Toast.makeText(AggiuntaPiattiActivity.this, "Esiste già un piatto con questo nome e portata", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Piatto p = new Piatto(editingId, nomePiattoNew, portataNew, nutrientiNew, Boolean.TRUE);
                        boolean ok = dataBaseHelper.updateOne(p);
                        if (ok) {
                            Toast.makeText(AggiuntaPiattiActivity.this, "Piatto aggiornato", Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            Toast.makeText(AggiuntaPiattiActivity.this, "Aggiornamento fallito", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        // create flow
                        if (dataBaseHelper.existsByNameAndPortata(nomePiattoNew, portataNew, -1)) {
                            Toast.makeText(AggiuntaPiattiActivity.this, "Esiste già un piatto con questo nome e portata", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Piatto piatto = new Piatto(-1, nomePiattoNew, portataNew, nutrientiNew, Boolean.TRUE);
                        boolean success = dataBaseHelper.addOne(piatto);

                        if (success){
                            Toast.makeText(AggiuntaPiattiActivity.this, "Piatto inserito con successo!", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        else {
                            Toast.makeText(AggiuntaPiattiActivity.this, "Ops! Qualcosa è andato storto...", Toast.LENGTH_SHORT).show();

                        }
                    }
                }
            });
        }

    }

    private void BackHomeActivity() {
        ImageButton homeBtn = (ImageButton)findViewById(R.id.homeBtn);
        if (homeBtn != null) {
            homeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    Intent backHome = new Intent(AggiuntaPiattiActivity.this, MainActivity.class);
                    startActivity(backHome);
                }
            });
        }
    }
    private void ListaPiattiActivity() {
        ImageButton homeBtn = (ImageButton)findViewById(R.id.ListaBtn);
        if (homeBtn != null) {
            homeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    Intent backHome = new Intent(AggiuntaPiattiActivity.this, ListaPiattiActivity.class);
                    startActivity(backHome);
                }
            });
        }
    }

}
