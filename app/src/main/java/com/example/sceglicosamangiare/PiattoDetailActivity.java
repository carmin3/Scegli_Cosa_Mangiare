package com.example.sceglicosamangiare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class PiattoDetailActivity extends AppCompatActivity {

    public static final String EXTRA_PIATTO_ID = "piatto_id";
    private PiattoRepository repo;
    private Piatto current;
    private int piattoId = -1;
    private static final int REQUEST_EDIT_PIATTO = 101;
    private static final String PREF_SHOW_RICETTA_WARNING = "show_ricetta_warning";
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

        Button btnModifica = findViewById(R.id.btnModifica);
        Button btnElimina = findViewById(R.id.btnElimina);
        Button btnRicetta = findViewById(R.id.btnRicetta);
        btnTogglePreferito = findViewById(R.id.btn_toggle_preferito);

        loadPiatto();

        if (btnRicetta != null) {
            btnRicetta.setOnClickListener(v -> {
                if (current != null && current.getNomePiatto() != null) {
                    SharedPreferences prefs = getSharedPreferences("ScegliCosaMangiarePrefs", MODE_PRIVATE);
                    boolean showWarning = prefs.getBoolean(PREF_SHOW_RICETTA_WARNING, true);

                    if (showWarning) {
                        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_ricetta_warning, null);
                        CheckBox checkBox = dialogView.findViewById(R.id.checkbox_dont_show_again);

                        new AlertDialog.Builder(this)
                                .setTitle("Avviso")
                                .setView(dialogView)
                                .setPositiveButton(R.string.ok_capito, (dialog, which) -> {
                                    if (checkBox.isChecked()) {
                                        prefs.edit().putBoolean(PREF_SHOW_RICETTA_WARNING, false).apply();
                                    }
                                    openRicetta();
                                })
                                .setNegativeButton(R.string.annulla, null)
                                .show();
                    } else {
                        openRicetta();
                    }
                }
            });
        }

        if (btnModifica != null) {
            btnModifica.setOnClickListener(v -> {
                if (current != null) {
                    Intent edit = new Intent(PiattoDetailActivity.this, AggiuntaPiattiActivity.class);
                    edit.putExtra("piatto_id", current.getId());
                    startActivityForResult(edit, REQUEST_EDIT_PIATTO);
                }
            });
        }

        if (btnElimina != null) {
            btnElimina.setOnClickListener(v -> {
                if (current == null) return;
                new AlertDialog.Builder(PiattoDetailActivity.this)
                        .setTitle("Elimina piatto")
                        .setMessage("Sei sicuro di voler eliminare questo piatto?")
                        .setPositiveButton("Elimina", (dialog, which) -> {
                            boolean deleted = repo.deleteOne(current.getId());
                            if (deleted) {
                                Toast.makeText(PiattoDetailActivity.this, "Piatto eliminato", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(PiattoDetailActivity.this, "Impossibile eliminare il piatto", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Annulla", null)
                        .show();
            });
        }

        if (btnTogglePreferito != null) {
            btnTogglePreferito.setOnClickListener(v -> {
                if (current == null) return;
                boolean newValue = !(current.getFavorito() != null && current.getFavorito());
                boolean ok = repo.setFavorite(current.getId(), newValue);
                if (ok) {
                    current.setFavorito(newValue);
                    if (newValue) {
                        btnTogglePreferito.setText(R.string.rimuovi_dai_preferiti);
                        btnTogglePreferito.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_star_filled, 0, 0, 0);
                        Toast.makeText(PiattoDetailActivity.this, "Aggiunto ai preferiti", Toast.LENGTH_SHORT).show();
                    } else {
                        btnTogglePreferito.setText(R.string.aggiungi_ai_preferiti);
                        btnTogglePreferito.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_star_outline, 0, 0, 0);
                        Toast.makeText(PiattoDetailActivity.this, "Rimosso dai preferiti", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(PiattoDetailActivity.this, "Operazione preferito fallita", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void BackHomeActivity() {
        ImageButton homeBtn = findViewById(R.id.homeBtn);
        if (homeBtn != null) {
            homeBtn.setOnClickListener(v -> {
                Intent backHome = new Intent(PiattoDetailActivity.this, MainActivity.class);
                startActivity(backHome);
            });
        }
    }

    private void openRicetta() {
        if (current != null && current.getNomePiatto() != null) {
            String query = "ricetta " + current.getNomePiatto();
            String url = "https://www.google.com/search?q=" + Uri.encode(query) + "&btnI=1";
            Intent intentRicetta = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intentRicetta);
        }
    }

    private void ListaPiattiActivity() {
        ImageButton listaBtn = findViewById(R.id.ListaBtn);
        if (listaBtn != null) {
            listaBtn.setOnClickListener(v -> {
                Intent goToLista = new Intent(PiattoDetailActivity.this, ListaPiattiActivity.class);
                startActivity(goToLista);
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
                btnTogglePreferito.setText(R.string.rimuovi_dai_preferiti);
                btnTogglePreferito.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_star_filled, 0, 0, 0);
            } else {
                btnTogglePreferito.setText(R.string.aggiungi_ai_preferiti);
                btnTogglePreferito.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_star_outline, 0, 0, 0);
            }
        }
    }
}
