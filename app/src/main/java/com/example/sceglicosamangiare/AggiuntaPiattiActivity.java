package com.example.sceglicosamangiare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

public class AggiuntaPiattiActivity extends AppCompatActivity {

    public String nomePiattoNew;
    public String portataNew;
    public String nutrientiNew;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aggiunta_piatti);

        BackHomeActivity();

        Button aggiungiPiattoBtn = (Button)findViewById(R.id.aggiungiPiattoBtn);
        aggiungiPiattoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Spinner spinnerPortata = findViewById(R.id.portataSpinner);
                Spinner spinnerNutrienti = findViewById(R.id.nutrientiSpinner);
                EditText TVnomeinput = (EditText) findViewById(R.id.nomeDelPiattoCasualeTV);

                Piatto piatto;

                //il primo if controlla il nome del piatto e lo scrive nella variabile nomePiattoNew
                if (TVnomeinput.getText().toString().equals("")) {
                    Toast.makeText(AggiuntaPiattiActivity.this, "Inserisci il nome del piatto", Toast.LENGTH_SHORT).show();
                }
                else {

                    nomePiattoNew = TVnomeinput.getText().toString();
                    nomePiattoNew = nomePiattoNew.toLowerCase();
                    nomePiattoNew = nomePiattoNew.substring(0, 1).toUpperCase() + nomePiattoNew.substring(1);
                    // il secondo if controlla lo spinner della portata e lo scrive nella variabile portataNew
                    if (spinnerPortata.getSelectedItem().toString().equals("Che portata è?")) {
                        Toast.makeText(AggiuntaPiattiActivity.this, "inserirsci che tipo di portata è", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        portataNew = spinnerPortata.getSelectedItem().toString();
                        // il terzo if controlla lo spinner dei nutrienti e lo riporta nella variabile nutrientiNew
                        if (spinnerNutrienti.getSelectedItem().toString().equals("Che nutrienti contiene?")){
                            Toast.makeText(AggiuntaPiattiActivity.this, "inserirsci che nutrienti ci sono", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            nutrientiNew = spinnerNutrienti.getSelectedItem().toString();

                            // iniziamo con il database:

                            // innanzitutto mettiamo tutti i dati che servono all'interno della variabile "piatto"
                            piatto = new Piatto(-1, nomePiattoNew, portataNew, nutrientiNew, true);

                            // richiamiamo la classe DataBaseHelper per poterne utilizzare i metodi. per fare ciò la abbiniamo ad una variabile chiamata dataBaseHelper
                            // in generale, quando vogliamo utilizzare un metodo di un'altra classe, prima abbiniamo la classe ad una variabile e quando richiamiamo il metodo usiamo la sintassi "variabileClasse.metodo"
                            DataBaseHelper dataBaseHelper = new DataBaseHelper(AggiuntaPiattiActivity.this);

                            // aggiungiamo il contenuto di "piatto" all'interno del nostro database, così come abbiamo specificato nel metodo "addOne" della classe DataBaseHelper
                            // N.B. qui, oltre ad aggiungere i dati al database (usando il codice "dataBaseHelper.addOne(piatto);" aggiungiamo boolean succes che non fa altro che inserire all'interno della variabile "success"
                            // il valore "true" se è andato tutto bene, "false" se c'è stato qualche problema con l'inserimento
                            boolean success = dataBaseHelper.addOne(piatto);

                            // mandiamo un toast per informare l'utente se tutto è andato a buon fine o meno
                            if (success == true){
                                Toast.makeText(AggiuntaPiattiActivity.this, "Piatto inserito con successo!", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(AggiuntaPiattiActivity.this, "Ops! Qualcosa è andato storto...", Toast.LENGTH_SHORT).show();

                            }
                        }
                    }
                }


            }
        });

    }

    private void BackHomeActivity() {
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