package com.example.sceglicosamangiare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
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
    private android.widget.LinearLayout filtriPiattoLL;
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
        goToAggiuntaPiattoActivity();
        // show the add button by default
        mostraTastoAggiunta();

    }

    @Override
    protected void onResume() {
        super.onResume();
        // refresh list when returning from other activities
        importaDatabase();
        setUpList();
    }

    private void setUpList() {
        listView = (ListView) findViewById(R.id.listView);
        if (listaPiatti == null || listaPiatti.isEmpty()) {
            if (listView != null) listView.setVisibility(View.GONE);
            if (emptyStateTV != null) emptyStateTV.setVisibility(View.VISIBLE);
            return;
        }
        if (emptyStateTV != null) emptyStateTV.setVisibility(View.GONE);
        if (listView != null) {
            PiattoListAdapter adapter = new PiattoListAdapter(getApplicationContext(), 0, listaPiatti);
            listView.setAdapter(adapter);

            // ensure clicks on rows open detail
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

    private void goToAggiuntaPiattiActivity() {
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

    // importiamo il database
    public void importaDatabase() {

        listaPiatti = new ArrayList<Piatto>();
        repo = new PiattoRepository(ListaPiattiActivity.this);
        listaPiatti = repo.getAllData();

    }

    // methods used elsewhere in class (kept for compatibility)
    private void initSearchWidgets() {}
    private void initWidgets() {}
    private void hideFilter() {}
    private void mostraTastoAggiunta() {}

}
