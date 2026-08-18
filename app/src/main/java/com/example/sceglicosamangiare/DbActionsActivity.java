package com.example.sceglicosamangiare;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

public class DbActionsActivity extends AppCompatActivity {

    private PiattoRepository repo;
    private DataBaseHelper personalDB;

    private final ActivityResultLauncher<Intent> exportLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    if (uri != null) {
                        try (OutputStream os = getContentResolver().openOutputStream(uri)) {
                            if (os != null) {
                                int n = personalDB.exportToOutputStream(os);
                                if (n >= 0) Toast.makeText(this, "Export completato: " + n + " ricette", Toast.LENGTH_LONG).show();
                                else Toast.makeText(this, "Export fallito", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(this, "Errore durante il salvataggio: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
    );

    private final ActivityResultLauncher<Intent> importLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    if (uri != null) {
                        try (InputStream is = getContentResolver().openInputStream(uri)) {
                            if (is != null) {
                                int n = personalDB.importFromInputStream(is);
                                if (n >= 0) Toast.makeText(this, "Import completato: " + n + " record", Toast.LENGTH_LONG).show();
                                else Toast.makeText(this, "Import fallito", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(this, "Errore durante l'importazione: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_db_actions);
        repo = new PiattoRepository(this);
        personalDB = new DataBaseHelper(this);

        Button export = findViewById(R.id.btn_export);
        Button imp = findViewById(R.id.btn_import);
        Button restore = findViewById(R.id.btn_restore);

        ImageButton homeBtn = findViewById(R.id.homeBtn);
        ImageButton settingsBtn = findViewById(R.id.settingsBtn);

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DbActionsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DbActionsActivity.this, ImpostazioniActivity.class);
                startActivity(intent);
            }
        });

        export.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("application/json");
                intent.putExtra(Intent.EXTRA_TITLE, "personal_export.json");
                exportLauncher.launch(intent);
            }
        });

        imp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("application/json");
                importLauncher.launch(intent);
            }
        });

        restore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(DbActionsActivity.this)
                        .setTitle("Conferma Ripristino")
                        .setMessage("Vuoi ripristinare le ricette originali? questo cancellerà tutte le modifiche al ricettaio, come ad esempio i piatti che hai aggiunto o modificato")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                boolean ok = personalDB.clearPersonal();
                                if (ok) {
                                    Toast.makeText(DbActionsActivity.this, "Ricette originali ripristinate", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(DbActionsActivity.this, "Ripristino fallito", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("Indietro", null)
                        .show();
            }
        });
    }
}
