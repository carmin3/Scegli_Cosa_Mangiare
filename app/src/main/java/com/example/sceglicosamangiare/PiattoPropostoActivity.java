package com.example.sceglicosamangiare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;


public class PiattoPropostoActivity extends AppCompatActivity {

    private DataBaseHelper dataBaseHelper;
    private ArrayList<Piatto> listaPiatti;
    private String proteinaScelta;
    private Random randomGenerator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_piatto_proposto);

        backHomeActivity();
        importaDatabase();
        sceltaCasualeProteina();
        sceltaCasualePiatto();
        refreshActivity();

    }

    private void refreshActivity() {
        ImageButton refreshBtn = (ImageButton)findViewById(R.id.refreshBtn);
        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                sceltaCasualeProteina();
                sceltaCasualePiatto();
            }
        });
    }


    public void importaDatabase() {

        listaPiatti = new ArrayList<Piatto>();
        dataBaseHelper = new DataBaseHelper(PiattoPropostoActivity.this);
        listaPiatti = dataBaseHelper.getAllData();

    }
    private void sceltaCasualeProteina() {
        //Scelta del tipo di proteina per il piatto casuale
        SceltaCasualeTipoProteina<String> itemDrops = new SceltaCasualeTipoProteina<>();

        itemDrops.addEntry("Carne Rossa",  5.0);
        itemDrops.addEntry("Carne Bianca",   20.0);
        itemDrops.addEntry("Pesce",  45.0);
        itemDrops.addEntry("Altro",   20.0);
        proteinaScelta = itemDrops.getProteina() + "";

    }

    private void sceltaCasualePiatto() {

        //filtrare lista piatti rispetto alla proteina scelta

        ArrayList<Piatto> piattiFiltratiPerProteina = new ArrayList<Piatto>();
        proteinaScelta = proteinaScelta.toLowerCase();

        for(Piatto piatto: listaPiatti)
        {
            if(piatto.getNutrienti().toLowerCase().contains(proteinaScelta))
            {
                piattiFiltratiPerProteina.add(piatto);
            }

        }


        randomGenerator = new Random();
        int index = randomGenerator.nextInt(piattiFiltratiPerProteina.size());
        Piatto piattoScelto = piattiFiltratiPerProteina.get(index);


        TextView nomeDelPiattoCasualeTV = (TextView) findViewById(R.id.nomeDelPiattoCasualeTV);
        nomeDelPiattoCasualeTV.setText(piattoScelto.getNomePiatto().toString());


    }

    private void backHomeActivity() {
        ImageButton homeBtn = (ImageButton)findViewById(R.id.homeBtn);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent backHome = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(backHome);
            }
        });
    }
}