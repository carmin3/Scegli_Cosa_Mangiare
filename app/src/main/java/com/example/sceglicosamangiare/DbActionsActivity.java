package com.example.sceglicosamangiare;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class DbActionsActivity extends AppCompatActivity {

    private PiattoRepository repo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_db_actions);
        repo = new PiattoRepository(this);

        Button export = findViewById(R.id.btn_export);
        Button imp = findViewById(R.id.btn_import);
        Button restore = findViewById(R.id.btn_restore);

        export.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DbActionsActivity.this, "Export non implementato: placeholder", Toast.LENGTH_SHORT).show();
            }
        });

        imp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DbActionsActivity.this, "Import non implementato: placeholder", Toast.LENGTH_SHORT).show();
            }
        });

        restore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // simple restore: remove all personal non-tombstone entries and tombstones
                Toast.makeText(DbActionsActivity.this, "Ripristino: rimuovo personal DB (placeholder)", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
