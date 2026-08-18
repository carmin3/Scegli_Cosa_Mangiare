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
import android.widget.TextView;

import java.util.ArrayList;

public class ListaPiattiActivity extends AppCompatActivity {

    private ListView listView;
    private String selectedFilter = "all";
    private String currentSearchText = "";
    private ImageButton filterBtn;
    private LinearLayout filtriPiattoLL;
    private boolean filterHidden = true;
    private Button aggiuntapiattoactivityBtn;
    private PiattoRepository repo;
    private ArrayList<Piatto> listaPiatti;
    private TextView emptyStateTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_piatti);

        emptyStateTV = findViewById(R.id.emptyStateTextView);

        importaDatabase();
        setUpList();
        initSearchWidgets();
        initWidgets();
        hideFilter();
        backHomeActivity();
        goToAggiuntaPiattiActivity();
        mostraTastoAggiunta();
    }

    @Override
    protected void onResume() {
        super.onResume();
        importaDatabase();
        setUpList();
    }

    private void setUpList() {
        listView = findViewById(R.id.listView);
        if (listaPiatti == null || listaPiatti.isEmpty()) {
            if (listView != null) listView.setVisibility(View.GONE);
            if (emptyStateTV != null) emptyStateTV.setVisibility(View.VISIBLE);
            return;
        }
        if (emptyStateTV != null) emptyStateTV.setVisibility(View.GONE);
        if (listView != null) {
            listView.setVisibility(View.VISIBLE);
            PiattoListAdapter adapter = new PiattoListAdapter(getApplicationContext(), 0, listaPiatti);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener((parent, view, position, id) -> {
                Piatto selected = (Piatto) parent.getItemAtPosition(position);
                if (selected != null) {
                    Intent detail = new Intent(ListaPiattiActivity.this, PiattoDetailActivity.class);
                    detail.putExtra(PiattoDetailActivity.EXTRA_PIATTO_ID, selected.getId());
                    startActivity(detail);
                }
            });
        }
    }

    private void backHomeActivity() {
        ImageButton homeBtn = findViewById(R.id.homeBtn);
        if (homeBtn != null) {
            homeBtn.setOnClickListener(v -> {
                Intent backHome = new Intent(ListaPiattiActivity.this, MainActivity.class);
                startActivity(backHome);
                finish();
            });
        }
    }

    private void goToAggiuntaPiattiActivity() {
        aggiuntapiattoactivityBtn = findViewById(R.id.aggiuntapiattoactivityBtn);
        if (aggiuntapiattoactivityBtn != null) {
            aggiuntapiattoactivityBtn.setOnClickListener(v -> {
                Intent vaiAdAggiuntaPiattiActivity = new Intent(ListaPiattiActivity.this, AggiuntaPiattiActivity.class);
                startActivity(vaiAdAggiuntaPiattiActivity);
            });
        }
    }

    public void importaDatabase() {
        if (repo == null) {
            repo = new PiattoRepository(ListaPiattiActivity.this);
        }

        ArrayList<Piatto> tuttiIPiatti = repo.getAllData();
        listaPiatti = new ArrayList<>();

        if (tuttiIPiatti != null) {
            for (Piatto p : tuttiIPiatti) {
                if (p.getTombstone() != null && p.getTombstone()) {
                    continue;
                }

                boolean matchesSearch = currentSearchText.isEmpty() ||
                        (p.getNomePiatto() != null && p.getNomePiatto().toLowerCase().contains(currentSearchText.toLowerCase()));

                boolean matchesFilter = true;
                if (selectedFilter.equalsIgnoreCase("personali")) {
                    matchesFilter = p.getPersonale() != null && p.getPersonale();
                } else if (!selectedFilter.equalsIgnoreCase("all")) {
                    matchesFilter = p.getPortata() != null && p.getPortata().equalsIgnoreCase(selectedFilter);
                }

                if (matchesSearch && matchesFilter) {
                    listaPiatti.add(p);
                }
            }
        }
    }

    private void initSearchWidgets() {
        SearchView searchView = findViewById(R.id.listaPiattiSearchView);
        if (searchView != null) {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    currentSearchText = newText != null ? newText : "";
                    importaDatabase();
                    setUpList();
                    return true;
                }
            });
        }
    }

    private void initWidgets() {
        filterBtn = findViewById(R.id.filterBtn);
        filtriPiattoLL = findViewById(R.id.filtriPiattoLL);

        if (filterBtn != null && filtriPiattoLL != null) {
            filterBtn.setOnClickListener(v -> {
                if (filterHidden) {
                    filtriPiattoLL.setVisibility(View.VISIBLE);
                    filterHidden = false;
                } else {
                    hideFilter();
                }
            });
        }
    }

    private void hideFilter() {
        if (filtriPiattoLL != null) {
            filtriPiattoLL.setVisibility(View.GONE);
            filterHidden = true;
        }
    }

    private void mostraTastoAggiunta() {
        if (aggiuntapiattoactivityBtn != null) {
            aggiuntapiattoactivityBtn.setVisibility(View.VISIBLE);
        }
    }

    // Metodi collegati agli android:onClick definite nell'XML
    public void tuttiFilterTapped(View view) {
        selectedFilter = "all";
        importaDatabase();
        setUpList();
    }

    public void primiFilterTapped(View view) {
        selectedFilter = "Primo";
        importaDatabase();
        setUpList();
    }

    public void secondiFilterTapped(View view) {
        selectedFilter = "Secondo";
        importaDatabase();
        setUpList();
    }

    public void contorniFilterTapped(View view) {
        selectedFilter = "Contorno";
        importaDatabase();
        setUpList();
    }

    public void piattiuniciFilterTapped(View view) {
        selectedFilter = "Piatto Unico";
        importaDatabase();
        setUpList();
    }

    public void personaliFilterTapped(View view) {
        selectedFilter = "personali";
        importaDatabase();
        setUpList();
    }
}