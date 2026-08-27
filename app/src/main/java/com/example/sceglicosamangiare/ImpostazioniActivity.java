package com.example.sceglicosamangiare;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ImpostazioniActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_impostazioni);

        ImageButton homeBtn = findViewById(R.id.homeBtn);
        homeBtn.setOnClickListener(v -> {
            Intent intent = new Intent(ImpostazioniActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        TextView backupRipristinoTV = findViewById(R.id.tv_backup_ripristino);
        backupRipristinoTV.setOnClickListener(v -> {
            Intent intent = new Intent(ImpostazioniActivity.this, DbActionsActivity.class);
            startActivity(intent);
        });

        TextView backupRipristinoCalendarioTV = findViewById(R.id.tv_backup_ripristino_calendario);
        backupRipristinoCalendarioTV.setOnClickListener(v -> {
            Intent intent = new Intent(ImpostazioniActivity.this, CalendarDbActionsActivity.class);
            startActivity(intent);
        });
    }
}
