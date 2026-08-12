package com.example.sceglicosamangiare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;
import java.util.ArrayList;


public class ListaPiattiActivity extends AppCompatActivity {

    private ListView listView;
    private String selectedFilter = "all";
    private String currentSearchText = "";
    private SearchView searchView;
    private ImageButton filterBtn;
    private LinearLayout filtriPiattoLL;
    private boolean filterHidden = true;
    private Button aggiuntapiattoactivityBtn;
    private DataBaseHelper dataBaseHelper;
    private ArrayList<Piatto> listaPiatti;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_piatti);

        importaDatabase();
        setUpList();
        initSearchWidgets();
        initWidgets();
        hideFilter();
        backHomeActivity();
        goToAggiuntaPiattoActivity();
        nascondiTastoAggiunta();

    }

    private void backHomeActivity() {
        ImageButton homeBtn = (ImageButton)findViewById(R.id.homeBtn);
        if (homeBtn != null) {
            homeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    Intent backHome = new Intent(ListaPiattiActivity.this, MainActivity.class);
                    startActivity(backHome);
                }
            });
        }
    }

    private void goToAggiuntaPiattoActivity() {
        Button vaiadaggiuntapiattiactivity = (Button)findViewById(R.id.aggiuntapiattoactivityBtn);
        if (vaiadaggiuntapiattiactivity != null) {
            vaiadaggiuntapiattiactivity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    Intent vaiAdAggiuntaPiattiActivity = new Intent(ListaPiattiActivity.this, AggiuntaPiattiActivity.class);
                    startActivity(vaiAdAggiuntaPiattiActivity);
                }
            });
        }
    }


    //importiamo il database - DA MODIFICARE
    public void importaDatabase() {

        listaPiatti = new ArrayList<Piatto>();
        dataBaseHelper = new DataBaseHelper(ListaPiattiActivity.this);
        listaPiatti = dataBaseHelper.getAllData();


//        new DatabasePiatti().setupData();
    }

    //con la prima istruzione andiamo a collegare l'oggetto "listview" che si trova nel frontend con la variabile listView presente in questa classe
    // poi diciamo di inviare tale listview al metodo "setAdapter"
    private void setUpList() {

        listView = (ListView) findViewById(R.id.listView);
        if (listView != null) {
            setAdapter(listaPiatti);
        }
    }


    // creiamo il metodo "setAdapter" al quale forniamo una "listaPiatti" e lui si occupa di applicargli la "forma" che abbiamo deciso nel nostro adapter
    public void setAdapter(ArrayList<Piatto> listaPiatti)
    {
        if (listView == null) return;
        PiattoListAdapter adapter = new PiattoListAdapter(getApplicationContext(), 0, listaPiatti);
        listView.setAdapter(adapter);
    }



    // qui comincia la parte di searching e filtering

    private void initSearchWidgets()
    {
        searchView = (SearchView) findViewById(R.id.listaPiattiSearchView);
        if (searchView == null) return;

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s)
            {
                currentSearchText = s != null ? s : "";
                ArrayList<Piatto> piattiFiltrati = new ArrayList<Piatto>();

                for(Piatto piatto: listaPiatti)
                {
                    if(piatto != null && piatto.getNomePiatto() != null && piatto.getNomePiatto().toLowerCase().contains(s.toLowerCase()))
                    {
                        if(selectedFilter.equals("all"))
                        {
                            piattiFiltrati.add(piatto);
                        }
                        else
                        {
                            if(piatto.getNomePiatto().toLowerCase().contains(s.toLowerCase())) //selectedFilter
                            {
                                piattiFiltrati.add(piatto);
                            }
                        }
                    }
                }
                setAdapter(piattiFiltrati);

                return false;
            }
        });
    }

    //vado ad accoppiare i tasti presenti nel layout con le variabili presenti in questa classe
    private void initWidgets() {
        filterBtn = (ImageButton) findViewById(R.id.filterBtn);
        filtriPiattoLL = (LinearLayout) findViewById(R.id.filtriPiattoLL);
        aggiuntapiattoactivityBtn = (Button) findViewById(R.id.aggiuntapiattoactivityBtn);
    }



    // L'obiettivo di queto metodo è creare una lista filtrata
    // gli devo fornire il valore del filtro (che viene inviato nel momento in cui si clicca sui bottoni dei filtri)
    private void filterList(String status)
    {
        selectedFilter = status;

        ArrayList<Piatto> piattiFiltrati = new ArrayList<Piatto>();

        // qui comincia la ricerca dei piatti che hanno come "Portata" il piatto selezionato dai filtri
        for(Piatto piatto: listaPiatti)
        {
            if(piatto != null && piatto.getPortata() != null && piatto.getPortata().toLowerCase().contains(status))
            {
                if(currentSearchText == null || currentSearchText.isEmpty())
                {
                    piattiFiltrati.add(piatto);
                }
                else
                {
                    if(piatto.getNomePiatto() != null && piatto.getNomePiatto().toLowerCase().contains(currentSearchText.toLowerCase()))
                    {
                        piattiFiltrati.add(piatto);
                    }
                }
            }
        }
        setAdapter(piattiFiltrati);
    }

    public void tuttiFilterTapped(View view)
    {
        selectedFilter = "all";
        if (searchView != null) {
            searchView.setQuery("", false);
            searchView.clearFocus();
        }

        setAdapter(listaPiatti);

        nascondiTastoAggiunta();
    }

    public void primiFilterTapped(View view)
    {
        filterList("primo");
        nascondiTastoAggiunta();

    }

    public void secondiFilterTapped(View view)
    {
        filterList("secondo");
        nascondiTastoAggiunta();

    }

    public void contorniFilterTapped(View view)
    {
        filterList("contorno");
        nascondiTastoAggiunta();

    }

    public void piattiuniciFilterTapped(View view)
    {
        filterList("piatto unico");
        nascondiTastoAggiunta();
    }

    public void personaliFilterTapped(View view)
    {
        ArrayList<Piatto> piattiPersonali = new ArrayList<Piatto>();

        for(Piatto piatto: listaPiatti)
        {
            if(Boolean.TRUE.equals(piatto.getPersonale()))
            {
                if(currentSearchText == null || currentSearchText.isEmpty())
                {
                    piattiPersonali.add(piatto);
                }
                else
                {
                    if(piatto.getNomePiatto() != null && piatto.getNomePiatto().toLowerCase().contains(currentSearchText.toLowerCase()))
                    {
                        piattiPersonali.add(piatto);
                    }
                }
            }
        }
        setAdapter(piattiPersonali);
        
        mostraTastoAggiunta();
    }



    // in questa sezione ci occupiamo di nascondere o visualizzare i bottoni
    public void showFilterTapped(View view)
    {
        if(filterHidden == true)
        {
            filterHidden = false;
            showFilter();
        }
        else
        {
            filterHidden = true;
            hideFilter();
        }
    }

    private void hideFilter()
    {
        if (filtriPiattoLL != null) filtriPiattoLL.setVisibility(View.GONE);
        if (filterBtn != null) filterBtn.setImageResource(R.drawable.filter_plus);
    }

    private void showFilter()
    {
        if (filtriPiattoLL != null) filtriPiattoLL.setVisibility(View.VISIBLE);
        if (filterBtn != null) filterBtn.setImageResource(R.drawable.filter_minus);
    }

    //tasto aggiunta piatto

    private void nascondiTastoAggiunta()
    {
        if (aggiuntapiattoactivityBtn != null) aggiuntapiattoactivityBtn.setVisibility(View.GONE);
    }

    private void mostraTastoAggiunta()
    {
        if (aggiuntapiattoactivityBtn != null) aggiuntapiattoactivityBtn.setVisibility(View.VISIBLE);
    }

}
