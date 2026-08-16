package com.example.sceglicosamangiare;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

public class DbActionsActivity extends AppCompatActivity {

    private PiattoRepository repo;
    private DataBaseHelper personalDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_db_actions);
        repo = new PiattoRepository(this);
        personalDB = new DataBaseHelper(this);

        Button export = findViewById(R.id.btn_export);
        Button imp = findViewById(R.id.btn_import);
        Button restore = findViewById(R.id.btn_restore);

        export.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File out = new File(getFilesDir(), "personal_export.json");
                int n = personalDB.exportToJsonFile(out);
                if (n >= 0) Toast.makeText(DbActionsActivity.this, "Export salvato: " + out.getAbsolutePath(), Toast.LENGTH_LONG).show();
                else Toast.makeText(DbActionsActivity.this, "Export fallito", Toast.LENGTH_SHORT).show();
            }
        });

        imp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File in = new File(getFilesDir(), "personal_export.json");
                if (!in.exists()) {
                    Toast.makeText(DbActionsActivity.this, "File import non trovato: " + in.getAbsolutePath(), Toast.LENGTH_LONG).show();
                    return;
                }
                int n = personalDB.importFromJsonFile(in);
                if (n >= 0) Toast.makeText(DbActionsActivity.this, "Import completato: " + n + " record", Toast.LENGTH_LONG).show();
                else Toast.makeText(DbActionsActivity.this, "Import fallito", Toast.LENGTH_SHORT).show();
            }
        });

        restore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean ok = personalDB.clearPersonal();
                if (ok) Toast.makeText(DbActionsActivity.this, "Personal DB svuotato", Toast.LENGTH_SHORT).show();
                else Toast.makeText(DbActionsActivity.this, "Ripristino fallito", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
