package com.example.sceglicosamangiare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class PiattoDetailActivity extends AppCompatActivity {

    public static final String EXTRA_PIATTO_ID = "piatto_id";
    private PiattoRepository repo;
    private Piatto current;
    private int piattoId = -1;
    private static final int REQUEST_EDIT_PIATTO = 101;
    private Button btnTogglePreferito;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_piatto_detail);

        repo = new PiattoRepository(this);

        // navigation
        BackHomeActivity();
        ListaPiattiActivity();

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_PIATTO_ID)) {
            piattoId = intent.getIntExtra(EXTRA_PIATTO_ID, -1);
        }

        final TextView nomeTV = findViewById(R.id.detailNome);
        final TextView portataTV = findViewById(R.id.detailPortata);
        final TextView nutrientiTV = findViewById(R.id.detailNutrienti);
        Button btnModifica = findViewById(R.id.btnModifica);
        Button btnElimina = findViewById(R.id.btnElimina);
        btnTogglePreferito = findViewById(R.id.btn_toggle_preferito);

        loadPiatto();

        if (btnModifica != null) {
            btnModifica.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (current != null) {
                        Intent edit = new Intent(PiattoDetailActivity.this, AggiuntaPiattiActivity.class);
                        edit.putExtra("piatto_id", current.getId());
                        startActivityForResult(edit, REQUEST_EDIT_PIATTO);
                    }
                }
            });
        }

        if (btnElimina != null) {
            btnElimina.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (current == null) return;
                    new AlertDialog.Builder(PiattoDetailActivity.this)
                            .setTitle("Elimina piatto")
                            .setMessage("Sei sicuro di voler eliminare questo piatto?")
                            .setPositiveButton("Elimina", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    boolean deleted = repo.deleteOne(current.getId());
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
        }

        if (btnTogglePreferito != null) {
            btnTogglePreferito.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (current == null) return;
                    boolean newValue = !(current.getFavorito() != null && current.getFavorito());
                    boolean ok = repo.setFavorite(current.getId(), newValue);
                    if (ok) {
                        current.setFavorito(newValue);
                        if (newValue) {
                            btnTogglePreferito.setText("RIMUOVI DAI PREFERITI");
                            btnTogglePreferito.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_star_filled, 0, 0, 0);
                            Toast.makeText(PiattoDetailActivity.this, "Aggiunto ai preferiti", Toast.LENGTH_SHORT).show();
                        } else {
                            btnTogglePreferito.setText("AGGIUNGI AI PREFERITI");
                            btnTogglePreferito.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_star_outline, 0, 0, 0);
                            Toast.makeText(PiattoDetailActivity.this, "Rimosso dai preferiti", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(PiattoDetailActivity.this, "Operazione preferito fallita", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void BackHomeActivity() {
        ImageButton homeBtn = (ImageButton) findViewById(R.id.homeBtn);
        if (homeBtn != null) {
            homeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent backHome = new Intent(PiattoDetailActivity.this, MainActivity.class);
                    startActivity(backHome);
                }
            });
        }
    }

    private void ListaPiattiActivity() {
        ImageButton listaBtn = (ImageButton) findViewById(R.id.ListaBtn);
        if (listaBtn != null) {
            listaBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent goToLista = new Intent(PiattoDetailActivity.this, ListaPiattiActivity.class);
                    startActivity(goToLista);
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_EDIT_PIATTO && resultCode == RESULT_OK) {
            Intent lista = new Intent(this, ListaPiattiActivity.class);
            lista.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(lista);
            finish();
        }
    }

    private void loadPiatto() {
        if (piattoId < 0) return;
        current = repo.getById(piattoId);
        if (current == null) return;
        TextView nomeTV = findViewById(R.id.detailNome);
        TextView portataTV = findViewById(R.id.detailPortata);
        TextView nutrientiTV = findViewById(R.id.detailNutrienti);

        if (nomeTV != null) nomeTV.setText(current.getNomePiatto());
        if (portataTV != null) portataTV.setText(current.getPortata());
        if (nutrientiTV != null) nutrientiTV.setText(current.getNutrienti());

        if (btnTogglePreferito != null) {
            boolean favorito = current.getFavorito() != null && current.getFavorito();
            if (favorito) {
                btnTogglePreferito.setText("RIMUOVI DAI PREFERITI");
                btnTogglePreferito.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_star_filled, 0, 0, 0);
            } else {
                btnTogglePreferito.setText("AGGIUNGI AI PREFERITI");
                btnTogglePreferito.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_star_outline, 0, 0, 0);
            }
        }
    }
}
