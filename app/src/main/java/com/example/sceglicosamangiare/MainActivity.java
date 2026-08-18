package com.example.sceglicosamangiare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button ProponiPiattoBtn = findViewById(R.id.ProponiPiattoBtn);
        if (ProponiPiattoBtn != null) {
            ProponiPiattoBtn.setOnClickListener(v -> {
                Intent ProponiPiattoIntent = new Intent(MainActivity.this, PiattoPropostoActivity.class);
                startActivity(ProponiPiattoIntent);
            });
        }

        ImageButton ListaBtn = findViewById(R.id.ListaBtn);
        if (ListaBtn != null) {
            ListaBtn.setOnClickListener(v -> {
                Intent ListaPiattiIntent = new Intent(MainActivity.this, ListaPiattiActivity.class);
                startActivity(ListaPiattiIntent);
            });
        }

        ImageButton settingsBtn = findViewById(R.id.settingsBtn);
        if (settingsBtn != null) {
            settingsBtn.setOnClickListener(v -> {
                Intent i = new Intent(MainActivity.this, ImpostazioniActivity.class);
                startActivity(i);
            });
        }

    }
}
