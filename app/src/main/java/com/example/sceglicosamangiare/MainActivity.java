package com.example.sceglicosamangiare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button ProponiPiattoBtn = (Button)findViewById(R.id.ProponiPiattoBtn);
        if (ProponiPiattoBtn != null) {
            ProponiPiattoBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent ProponiPiattoIntent = new Intent(MainActivity.this, PiattoPropostoActivity.class);
                    startActivity(ProponiPiattoIntent);
                }
            });
        }

        ImageButton ListaBtn = (ImageButton)findViewById(R.id.ListaBtn);
        if (ListaBtn != null) {
            ListaBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent ListaPiattiIntent = new Intent(MainActivity.this, ListaPiattiActivity.class);
                    startActivity(ListaPiattiIntent);
                }
            });
        }

    }
}
