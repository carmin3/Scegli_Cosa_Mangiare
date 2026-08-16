package com.example.sceglicosamangiare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import java.util.ArrayList;

public class ListaPiattiActivity extends AppCompatActivity {

    private ListView listView;
    private String selectedFilter = "all";
    private String currentSearchText = "";
    private SearchView searchView;
    private ImageButton filterBtn;
    private LinearLayout filtriPiattoLL;
    private boolean filterHidden = true;
    private DataBaseHelper dataBaseHelper;
    private ArrayList<Piatto> listaPiatti;
    private TextView emptyStateTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_piatti);

        listView = findViewById(R.id.listView);
        emptyStateTV = findViewById(R.id.emptyStateTextView);

        importaDatabase();
        initWidgets();
        initSearchWidgets();
        hideFilter(); // Nasconde i filtri E il pulsante di aggiunta all'inizio
        backHomeActivity();
        goToAggiuntaPiattoActivity();

        applyFilters();
    }

    @Override
    protected void onResume() {
        super.onResume();
        importaDatabase();
        applyFilters();
    }

    public void importaDatabase() {
        dataBaseHelper = new DataBaseHelper(ListaPiattiActivity.this);
        listaPiatti = dataBaseHelper.getAllData();
        if (listaPiatti == null) {
            listaPiatti = new ArrayList<>();
        }
    }

    private void initWidgets() {
        filterBtn = findViewById(R.id.filterBtn);
        filtriPiattoLL = findViewById(R.id.filtriPiattoLL);

        if (filterBtn != null) {
            filterBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showFilterTapped(v);
                }
            });
        }
    }

    private void initSearchWidgets() {
        searchView = findViewById(R.id.listaPiattiSearchView);
        if (searchView == null) return;

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                currentSearchText = s != null ? s : "";
                applyFilters();
                return false;
            }
        });
    }

    private void applyFilters() {
        ArrayList<Piatto> piattiFiltrati = new ArrayList<>();

        for (Piatto piatto : listaPiatti) {
            if (piatto == null) continue;

            boolean matchesSearch = currentSearchText.isEmpty() ||
                    (piatto.getNomePiatto() != null && piatto.getNomePiatto().toLowerCase().contains(currentSearchText.toLowerCase()));

            boolean matchesFilter = false;
            if (selectedFilter.equals("all")) {
                matchesFilter = true;
            } else if (selectedFilter.equals("personali")) {
                matchesFilter = Boolean.TRUE.equals(piatto.getPersonale());
            } else {
                matchesFilter = piatto.getPortata() != null && piatto.getPortata().toLowerCase().contains(selectedFilter);
            }

            if (matchesSearch && matchesFilter) {
                piattiFiltrati.add(piatto);
            }
        }

        setAdapter(piattiFiltrati);
    }

    public void setAdapter(ArrayList<Piatto> lista) {
        if (listView == null) return;

        if (lista == null || lista.isEmpty()) {
            listView.setVisibility(View.GONE);
            if (emptyStateTV != null) emptyStateTV.setVisibility(View.VISIBLE);
        } else {
            listView.setVisibility(View.VISIBLE);
            if (emptyStateTV != null) emptyStateTV.setVisibility(View.GONE);

            PiattoListAdapter adapter = new PiattoListAdapter(this, 0, lista);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Piatto selected = (Piatto) parent.getItemAtPosition(position);
                    if (selected != null) {
                        Intent detail = new Intent(ListaPiattiActivity.this, PiattoDetailActivity.class);
                        detail.putExtra(PiattoDetailActivity.EXTRA_PIATTO_ID, selected.getId());
                        startActivity(detail);
                    }
                }
            });
        }
    }

    private void backHomeActivity() {
        ImageButton homeBtn = findViewById(R.id.homeBtn);
        if (homeBtn != null) {
            homeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent backHome = new Intent(ListaPiattiActivity.this, MainActivity.class);
                    startActivity(backHome);
                }
            });
        }
    }

    private void goToAggiuntaPiattoActivity() {
        Button vaiadaggiuntapiattiactivity = findViewById(R.id.aggiuntapiattoactivityBtn);
        if (vaiadaggiuntapiattiactivity != null) {
            vaiadaggiuntapiattiactivity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent vaiAdAggiuntaPiattiActivity = new Intent(ListaPiattiActivity.this, AggiuntaPiattiActivity.class);
                    startActivity(vaiAdAggiuntaPiattiActivity);
                }
            });
        }
    }

    public void tuttiFilterTapped(View view) {
        selectedFilter = "all";
        if (searchView != null) {
            searchView.setQuery("", false);
            searchView.clearFocus();
        }
        applyFilters();
    }

    public void primiFilterTapped(View view) {
        selectedFilter = "primo";
        applyFilters();
    }

    public void secondiFilterTapped(View view) {
        selectedFilter = "secondo";
        applyFilters();
    }

    public void contorniFilterTapped(View view) {
        selectedFilter = "contorno";
        applyFilters();
    }

    public void piattiuniciFilterTapped(View view) {
        selectedFilter = "piatto unico";
        applyFilters();
    }

    public void personaliFilterTapped(View view) {
        selectedFilter = "personali";
        applyFilters();
    }

    public void showFilterTapped(View view) {
        if (filterHidden) {
            filterHidden = false;
            showFilter();
        } else {
            filterHidden = true;
            hideFilter();
        }
    }

    private void hideFilter() {
        if (filtriPiattoLL != null) filtriPiattoLL.setVisibility(View.GONE);
        if (filterBtn != null) filterBtn.setImageResource(R.drawable.filter_plus);
    }

    private void showFilter() {
        if (filtriPiattoLL != null) filtriPiattoLL.setVisibility(View.VISIBLE);
        if (filterBtn != null) filterBtn.setImageResource(R.drawable.filter_minus);
    }
}