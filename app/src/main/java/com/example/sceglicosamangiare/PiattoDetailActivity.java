package com.example.sceglicosamangiare;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class PiattoDetailActivity extends AppCompatActivity {

    public static final String EXTRA_PIATTO_ID = "piatto_id";
    private DataBaseHelper dataBaseHelper;
    private Piatto current;
    private int piattoId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_piatto_detail);

        BackHomeActivity();

        dataBaseHelper = new DataBaseHelper(PiattoDetailActivity.this);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_PIATTO_ID)) {
            piattoId = intent.getIntExtra(EXTRA_PIATTO_ID, -1);
        }

        Button btnModifica = findViewById(R.id.btnModifica);
        Button btnElimina = findViewById(R.id.btnElimina);
        final Button btnPreferito = findViewById(R.id.btnPreferito);

        // load views and data
        loadPiatto();

        btnModifica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (current != null) {
                    Intent edit = new Intent(PiattoDetailActivity.this, AggiuntaPiattiActivity.class);
                    edit.putExtra("piatto_id", current.getId());
                    startActivity(edit);
                    finish();
                }
            }
        });

        btnElimina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (current == null) return;
                new AlertDialog.Builder(PiattoDetailActivity.this)
                        .setTitle("Elimina piatto")
                        .setMessage("Sei sicuro di voler eliminare questo piatto?")
                        .setPositiveButton("Elimina", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                boolean deleted = dataBaseHelper.deleteOne(current.getId());
                                if (deleted) {
                                    Toast.makeText(PiattoDetailActivity.this, "Piatto eliminato", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Toast.makeText(PiattoDetailActivity.this, "Impossibile eliminare il piatto", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("Annulla", null)
                        .show();
            }
        });

        btnPreferito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (current == null) return;
                boolean newValue = !(current.getFavorito() != null && current.getFavorito());
                boolean ok = dataBaseHelper.setFavorite(current.getId(), newValue);
                if (ok) {
                    current.setFavorito(newValue);
                    btnPreferito.setText(newValue ? "Rimuovi preferito" : "Aggiungi ai preferiti");
                    Toast.makeText(PiattoDetailActivity.this, newValue ? "Aggiunto ai preferiti" : "Rimosso dai preferiti", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PiattoDetailActivity.this, "Operazione preferito fallita", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void loadPiatto() {
        if (piattoId < 0) return;
        current = dataBaseHelper.getById(piattoId);
        if (current == null) return;

        View nomeView = findViewById(R.id.detailNome);
        Spinner portataSpinner = findViewById(R.id.detailPortata);
        Spinner nutrientiSpinner = findViewById(R.id.detailNutrienti);
        Button btnPreferito = findViewById(R.id.btnPreferito);

        // Nome: prefer EditText, fallback to Spinner if layout still uses Spinner
        if (nomeView instanceof EditText) {
            ((EditText) nomeView).setText(current.getNomePiatto() != null ? current.getNomePiatto() : "");
        } else if (nomeView instanceof Spinner) {
            ensureSpinnerSelection((Spinner) nomeView, current.getNomePiatto());
        }

        ensureSpinnerSelection(portataSpinner, current.getPortata());
        ensureSpinnerSelection(nutrientiSpinner, current.getNutrienti());

        if (btnPreferito != null) btnPreferito.setText(current.getFavorito() != null && current.getFavorito() ? "Rimuovi preferito" : "Aggiungi ai preferiti");
    }

    private void ensureSpinnerSelection(Spinner spinner, String value) {
        if (spinner == null) return;
        SpinnerAdapter adapter = spinner.getAdapter();
        String normalized = value != null ? value.trim() : "";

        if (adapter == null || adapter.getCount() == 0) {
            ArrayList<String> list = new ArrayList<>();
            list.add(normalized);
            ArrayAdapter<String> aa = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list);
            aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(aa);
            spinner.setSelection(0);
            return;
        }

        for (int i = 0; i < adapter.getCount(); i++) {
            Object item = adapter.getItem(i);
            if (item != null && item.toString().trim().equalsIgnoreCase(normalized)) {
                spinner.setSelection(i);
                return;
            }
        }

        // value not found in existing adapter -> build a new adapter with value first
        ArrayList<String> combined = new ArrayList<>();
        if (!normalized.isEmpty()) combined.add(normalized);
        for (int i = 0; i < adapter.getCount(); i++) {
            Object item = adapter.getItem(i);
            if (item != null) {
                String s = item.toString();
                if (!s.equalsIgnoreCase(normalized)) combined.add(s);
            }
        }
        ArrayAdapter<String> aa = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, combined);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(aa);
        spinner.setSelection(0);
    }
    private void BackHomeActivity() {
        ImageButton homeBtn = (ImageButton)findViewById(R.id.homeBtn);
        if (homeBtn != null) {
            homeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    Intent backHome = new Intent(PiattoDetailActivity.this, MainActivity.class);
                    startActivity(backHome);
                }
            });
        }
    }

}
