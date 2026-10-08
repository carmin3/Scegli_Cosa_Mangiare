package com.example.sceglicosamangiare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ListaPiattiActivity extends AppCompatActivity {

    private ListView listView;
    private String currentSearchText = "";
    private ImageButton filterBtn;
    private LinearLayout filtriPiattoLL;
    private boolean filterHidden = true;
    private Button aggiuntapiattoactivityBtn;
    private PiattoRepository repo;
    private ArrayList<Piatto> listaPiatti;
    private TextView emptyStateTV;

    private Spinner filterSpinnerPortata;
    private Spinner filterSpinnerBase;
    private Spinner filterSpinnerNutrienti;
    private Spinner filterSpinnerGusto;
    private CheckBox filterCheckBoxPreferiti;

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
            PiattoListAdapter adapter = new PiattoListAdapter(this, listaPiatti, piatto -> {
                repo.setFavorite(piatto.getId(), true);
                piatto.setFavorito(true);
                Toast.makeText(ListaPiattiActivity.this, "Aggiunto ai preferiti: " + piatto.getNomePiatto(), Toast.LENGTH_SHORT).show();
                importaDatabase();
                setUpList();
            });
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

        String selPortata = (filterSpinnerPortata != null && filterSpinnerPortata.getSelectedItem() != null) ? filterSpinnerPortata.getSelectedItem().toString() : "";
        String selBase = (filterSpinnerBase != null && filterSpinnerBase.getSelectedItem() != null) ? filterSpinnerBase.getSelectedItem().toString() : "";
        String selNutrienti = (filterSpinnerNutrienti != null && filterSpinnerNutrienti.getSelectedItem() != null) ? filterSpinnerNutrienti.getSelectedItem().toString() : "";
        String selGusto = (filterSpinnerGusto != null && filterSpinnerGusto.getSelectedItem() != null) ? filterSpinnerGusto.getSelectedItem().toString() : "";
        boolean reqFavorito = (filterCheckBoxPreferiti != null && filterCheckBoxPreferiti.isChecked());

        if (tuttiIPiatti != null) {
            for (Piatto p : tuttiIPiatti) {
                if (p.getTombstone() != null && p.getTombstone()) {
                    continue;
                }

                boolean matchesSearch = currentSearchText.isEmpty() ||
                        (p.getNomePiatto() != null && p.getNomePiatto().toLowerCase().contains(currentSearchText.toLowerCase()));

                boolean matchesPortata = selPortata.isEmpty() || selPortata.equalsIgnoreCase("Che portata è?") ||
                        (p.getPortata() != null && p.getPortata().equalsIgnoreCase(selPortata));

                boolean matchesBase = selBase.isEmpty() || selBase.equalsIgnoreCase("Base del Piatto") ||
                        (p.getNutrienti() != null && p.getNutrienti().equalsIgnoreCase(selBase));

                boolean matchesNutrienti = selNutrienti.isEmpty() || selNutrienti.equalsIgnoreCase("Nutrienti") ||
                        (p.getDominanzaNutrizionale() != null && p.getDominanzaNutrizionale().equalsIgnoreCase(selNutrienti));

                boolean matchesGusto = selGusto.isEmpty() || selGusto.equalsIgnoreCase("Gusto") ||
                        (p.getProfiloGustativo() != null && p.getProfiloGustativo().equalsIgnoreCase(selGusto));

                boolean matchesFavorito = !reqFavorito || (p.getFavorito() != null && p.getFavorito());

                if (matchesSearch && matchesPortata && matchesBase && matchesNutrienti && matchesGusto && matchesFavorito) {
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

        filterSpinnerPortata = findViewById(R.id.filterSpinnerPortata);
        filterSpinnerBase = findViewById(R.id.filterSpinnerBase);
        filterSpinnerNutrienti = findViewById(R.id.filterSpinnerNutrienti);
        filterSpinnerGusto = findViewById(R.id.filterSpinnerGusto);
        filterCheckBoxPreferiti = findViewById(R.id.filterCheckBoxPreferiti);

        setupSpinnerAdapter(filterSpinnerPortata, R.array.cheportata);
        setupSpinnerAdapter(filterSpinnerBase, R.array.chenutrienti);
        setupSpinnerAdapter(filterSpinnerNutrienti, R.array.chedominanza);
        setupSpinnerAdapter(filterSpinnerGusto, R.array.chegusto);

        AdapterView.OnItemSelectedListener spinnerListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (view != null) {
                    view.setAlpha(position == 0 ? 0.5f : 1.0f);
                }
                importaDatabase();
                setUpList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };

        if (filterSpinnerPortata != null) filterSpinnerPortata.setOnItemSelectedListener(spinnerListener);
        if (filterSpinnerBase != null) filterSpinnerBase.setOnItemSelectedListener(spinnerListener);
        if (filterSpinnerNutrienti != null) filterSpinnerNutrienti.setOnItemSelectedListener(spinnerListener);
        if (filterSpinnerGusto != null) filterSpinnerGusto.setOnItemSelectedListener(spinnerListener);

        if (filterCheckBoxPreferiti != null) {
            filterCheckBoxPreferiti.setAlpha(filterCheckBoxPreferiti.isChecked() ? 1.0f : 0.5f);
            filterCheckBoxPreferiti.setOnCheckedChangeListener((buttonView, isChecked) -> {
                buttonView.setAlpha(isChecked ? 1.0f : 0.5f);
                importaDatabase();
                setUpList();
            });
        }

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

    private void setupSpinnerAdapter(Spinner spinner, int arrayResId) {
        if (spinner == null) return;
        android.widget.ArrayAdapter<CharSequence> adapter = new android.widget.ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, getResources().getTextArray(arrayResId)) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                view.setAlpha(position == 0 ? 0.5f : 1.0f);
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                view.setAlpha(1.0f);
                return view;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
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
}
