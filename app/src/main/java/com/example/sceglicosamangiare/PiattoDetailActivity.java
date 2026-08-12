package com.example.sceglicosamangiare;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class PiattoDetailActivity extends AppCompatActivity {

    public static final String EXTRA_PIATTO_ID = "piatto_id";
    private DataBaseHelper dataBaseHelper;
    private Piatto current;
    private int piattoId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_piatto_detail);

        dataBaseHelper = new DataBaseHelper(PiattoDetailActivity.this);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_PIATTO_ID)) {
            piattoId = intent.getIntExtra(EXTRA_PIATTO_ID, -1);
        }

        final TextView nomeTV = findViewById(R.id.detailNome);
        final TextView portataTV = findViewById(R.id.detailPortata);
        final TextView nutrientiTV = findViewById(R.id.detailNutrienti);
        Button btnModifica = findViewById(R.id.btnModifica);
        Button btnElimina = findViewById(R.id.btnElimina);
        final Button btnPreferito = findViewById(R.id.btnPreferito);

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
        TextView nomeTV = findViewById(R.id.detailNome);
        TextView portataTV = findViewById(R.id.detailPortata);
        TextView nutrientiTV = findViewById(R.id.detailNutrienti);
        Button btnPreferito = findViewById(R.id.btnPreferito);

        if (nomeTV != null) nomeTV.setText(current.getNomePiatto());
        if (portataTV != null) portataTV.setText(current.getPortata());
        if (nutrientiTV != null) nutrientiTV.setText(current.getNutrienti());
        if (btnPreferito != null) btnPreferito.setText(current.getFavorito() != null && current.getFavorito() ? "Rimuovi preferito" : "Aggiungi ai preferiti");
    }
}
