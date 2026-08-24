package com.example.sceglicosamangiare;

import androidx.core.content.ContextCompat;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListPopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalendarioActivity extends AppCompatActivity {

    private String selectedDate;
    private DataBaseHelper dbHelper;
    private PiattoRepository repository;
    private Spinner spinnerProtPranzo, spinnerProtCena;
    private EditText etPranzoPrimo, etPranzoSecondo, etPranzoContorno, etPranzoPiattoUnico;
    private EditText etCenaPrimo, etCenaSecondo, etCenaContorno, etCenaPiattoUnico;
    private Button btnCreaMenu, btnSalvaPiano;
    private ImageButton btnActionPPrimo, btnActionPSecondo, btnActionPContorno, btnActionPPiattoUnico;
    private ImageButton btnActionCPrimo, btnActionCSecondo, btnActionCContorno, btnActionCPiattoUnico;
    private TextView editDateTV;
    private ListPopupWindow popupWindow;
    private String[] proteine = {"Carne Rossa", "Carne Bianca", "Pesce", "Altro", "Casuale"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendario);

        dbHelper = new DataBaseHelper(this);
        repository = new PiattoRepository(this);
        selectedDate = getIntent().getStringExtra("selectedDate");

        editDateTV = findViewById(R.id.editDateTV);
        spinnerProtPranzo = findViewById(R.id.spinnerProtPranzo);
        spinnerProtCena = findViewById(R.id.spinnerProtCena);
        
        etPranzoPrimo = findViewById(R.id.etPranzoPrimo);
        etPranzoSecondo = findViewById(R.id.etPranzoSecondo);
        etPranzoContorno = findViewById(R.id.etPranzoContorno);
        etPranzoPiattoUnico = findViewById(R.id.etPranzoPiattoUnico);
        etCenaPrimo = findViewById(R.id.etCenaPrimo);
        etCenaSecondo = findViewById(R.id.etCenaSecondo);
        etCenaContorno = findViewById(R.id.etCenaContorno);
        etCenaPiattoUnico = findViewById(R.id.etCenaPiattoUnico);

        btnActionPPrimo = findViewById(R.id.btnActionPranzoPrimo);
        btnActionPSecondo = findViewById(R.id.btnActionPranzoSecondo);
        btnActionPContorno = findViewById(R.id.btnActionPranzoContorno);
        btnActionPPiattoUnico = findViewById(R.id.btnActionPranzoPiattoUnico);
        btnActionCPrimo = findViewById(R.id.btnActionCenaPrimo);
        btnActionCSecondo = findViewById(R.id.btnActionCenaSecondo);
        btnActionCContorno = findViewById(R.id.btnActionCenaContorno);
        btnActionCPiattoUnico = findViewById(R.id.btnActionCenaPiattoUnico);

        setupSpinners();
        displayDate();
        loadData();

        findViewById(R.id.homeBtn).setOnClickListener(v -> finish());
        btnSalvaPiano = findViewById(R.id.btnSalvaPiano);
        btnSalvaPiano.setOnClickListener(v -> savePiano());

        btnCreaMenu = findViewById(R.id.btnCreaMenu);
        btnCreaMenu.setOnClickListener(v -> generateMenu());

        setupSearch(etPranzoPrimo, btnActionPPrimo);
        setupSearch(etPranzoSecondo, btnActionPSecondo);
        setupSearch(etPranzoContorno, btnActionPContorno);
        setupSearch(etPranzoPiattoUnico, btnActionPPiattoUnico);
        setupSearch(etCenaPrimo, btnActionCPrimo);
        setupSearch(etCenaSecondo, btnActionCSecondo);
        setupSearch(etCenaContorno, btnActionCContorno);
        setupSearch(etCenaPiattoUnico, btnActionCPiattoUnico);

        setupRandomButton(btnActionPPrimo, etPranzoPrimo, "Primo", spinnerProtPranzo);
        setupRandomButton(btnActionPSecondo, etPranzoSecondo, "Secondo", spinnerProtPranzo);
        setupRandomButton(btnActionPContorno, etPranzoContorno, "Contorno", spinnerProtPranzo);
        setupRandomButton(btnActionPPiattoUnico, etPranzoPiattoUnico, "Piatto Unico", spinnerProtPranzo);
        setupRandomButton(btnActionCPrimo, etCenaPrimo, "Primo", spinnerProtCena);
        setupRandomButton(btnActionCSecondo, etCenaSecondo, "Secondo", spinnerProtCena);
        setupRandomButton(btnActionCContorno, etCenaContorno, "Contorno", spinnerProtCena);
        setupRandomButton(btnActionCPiattoUnico, etCenaPiattoUnico, "Piatto Unico", spinnerProtCena);
    }

    private void setupSpinners() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, proteine);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProtPranzo.setAdapter(adapter);
        spinnerProtCena.setAdapter(adapter);
    }

    private void displayDate() {
        try {
            SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            Date date = dbFormat.parse(selectedDate);
            if (date != null) {
                SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE d MMMM", Locale.ITALIAN);
                String dateStr = dayFormat.format(date);
                dateStr = dateStr.substring(0, 1).toUpperCase() + dateStr.substring(1);
                editDateTV.setText(dateStr);
            } else {
                editDateTV.setText(selectedDate);
            }
        } catch (Exception e) {
            editDateTV.setText(selectedDate);
        }
    }

    private void loadData() {
        PianoPasto piano = dbHelper.getPianoPastoByDate(selectedDate);
        if (piano != null) {
            loadDish(etPranzoPrimo, btnActionPPrimo, piano.getPranzoPrimo());
            loadDish(etPranzoSecondo, btnActionPSecondo, piano.getPranzoSecondo());
            loadDish(etPranzoContorno, btnActionPContorno, piano.getPranzoContorno());
            loadDish(etPranzoPiattoUnico, btnActionPPiattoUnico, piano.getPranzoPiattoUnico());
            loadDish(etCenaPrimo, btnActionCPrimo, piano.getCenaPrimo());
            loadDish(etCenaSecondo, btnActionCSecondo, piano.getCenaSecondo());
            loadDish(etCenaContorno, btnActionCContorno, piano.getCenaContorno());
            loadDish(etCenaPiattoUnico, btnActionCPiattoUnico, piano.getCenaPiattoUnico());

            setSpinnerSelection(spinnerProtPranzo, piano.getProteinaPranzo());
            setSpinnerSelection(spinnerProtCena, piano.getProteinaCena());
        }
    }

    private void loadDish(EditText et, ImageButton btn, String nome) {
        if (nome == null || nome.isEmpty()) {
            clearDishState(et, btn);
            return;
        }
        et.setText(nome);
        // Try to see if this name corresponds to a unique dish in the DB to restore state
        ArrayList<Piatto> matches = repository.searchByName(nome);
        Piatto exactMatch = null;
        for (Piatto p : matches) {
            if (p.getNomePiatto().equalsIgnoreCase(nome)) {
                exactMatch = p;
                break;
            }
        }
        if (exactMatch != null) {
            setDishSelectedState(et, btn, exactMatch);
        } else {
            clearDishState(et, btn);
            et.setText(nome);
        }
    }

    private void setSpinnerSelection(Spinner spinner, String value) {
        for (int i = 0; i < proteine.length; i++) {
            if (proteine[i].equalsIgnoreCase(value)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    private void savePiano() {
        PianoPasto piano = new PianoPasto(
                selectedDate,
                spinnerProtPranzo.getSelectedItem().toString(),
                etPranzoPrimo.getText().toString().trim(),
                etPranzoSecondo.getText().toString().trim(),
                etPranzoContorno.getText().toString().trim(),
                etPranzoPiattoUnico.getText().toString().trim(),
                spinnerProtCena.getSelectedItem().toString(),
                etCenaPrimo.getText().toString().trim(),
                etCenaSecondo.getText().toString().trim(),
                etCenaContorno.getText().toString().trim(),
                etCenaPiattoUnico.getText().toString().trim()
        );
        dbHelper.savePianoPasto(piano);
        Toast.makeText(this, "Piano salvato", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void generateMenu() {
        // Clear previous dishes first
        clearDishState(etPranzoPrimo, btnActionPPrimo);
        clearDishState(etPranzoSecondo, btnActionPSecondo);
        clearDishState(etPranzoContorno, btnActionPContorno);
        clearDishState(etPranzoPiattoUnico, btnActionPPiattoUnico);
        clearDishState(etCenaPrimo, btnActionCPrimo);
        clearDishState(etCenaSecondo, btnActionCSecondo);
        clearDishState(etCenaContorno, btnActionCContorno);
        clearDishState(etCenaPiattoUnico, btnActionCPiattoUnico);

        // --- PRANZO ---
        String proteinaPranzo = spinnerProtPranzo.getSelectedItem().toString();
        if (proteinaPranzo.equalsIgnoreCase("Casuale")) {
            proteinaPranzo = getWeightedRandomProteina();
            setSpinnerSelection(spinnerProtPranzo, proteinaPranzo);
        }

        Piatto pP1 = repository.pickRandomByPortataAndProteina("Primo", proteinaPranzo);
        Piatto pP2 = repository.pickRandomByPortataAndProteina("Secondo", proteinaPranzo);
        Piatto pP3 = repository.pickRandomByPortataAndProteina("Contorno", proteinaPranzo);

        if (pP1 != null) setDishSelectedState(etPranzoPrimo, btnActionPPrimo, pP1);
        if (pP2 != null) setDishSelectedState(etPranzoSecondo, btnActionPSecondo, pP2);
        if (pP3 != null) setDishSelectedState(etPranzoContorno, btnActionPContorno, pP3);

        // --- CENA ---
        String proteinaCena = spinnerProtCena.getSelectedItem().toString();
        if (proteinaCena.equalsIgnoreCase("Casuale")) {
            proteinaCena = getWeightedRandomProteina();
            setSpinnerSelection(spinnerProtCena, proteinaCena);
        }

        Piatto cP1 = repository.pickRandomByPortataAndProteina("Primo", proteinaCena);
        Piatto cP2 = repository.pickRandomByPortataAndProteina("Secondo", proteinaCena);
        Piatto cP3 = repository.pickRandomByPortataAndProteina("Contorno", proteinaCena);

        if (cP1 != null) setDishSelectedState(etCenaPrimo, btnActionCPrimo, cP1);
        if (cP2 != null) setDishSelectedState(etCenaSecondo, btnActionCSecondo, cP2);
        if (cP3 != null) setDishSelectedState(etCenaContorno, btnActionCContorno, cP3);

        Toast.makeText(this, "Menù generato!", Toast.LENGTH_SHORT).show();
    }

    private String getWeightedRandomProteina() {
        SceltaCasualeTipoProteina<String> itemDrops = new SceltaCasualeTipoProteina<>();
        itemDrops.addEntry("Carne Rossa", 5.0);
        itemDrops.addEntry("Carne Bianca", 20.0);
        itemDrops.addEntry("Pesce", 45.0);
        itemDrops.addEntry("Altro", 20.0);
        return itemDrops.getProteina();
    }

    private void setupSearch(EditText et, ImageButton btn) {
        et.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!et.isEnabled()) return;
                String query = s.toString().trim();
                if (query.length() >= 2) {
                    ArrayList<Piatto> results = repository.searchByName(query);
                    if (!results.isEmpty() && results.size() <= 5) {
                        showSuggestions(et, btn, results);
                    } else {
                        dismissPopup();
                    }
                } else {
                    dismissPopup();
                }
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void setupRandomButton(ImageButton btn, EditText et, String portata, Spinner spinner) {
        btn.setOnClickListener(v -> {
            if (et.isEnabled()) {
                String proteina = spinner.getSelectedItem().toString();
                Piatto p = repository.pickRandomByPortataAndProteina(portata, proteina);
                if (p != null) {
                    setDishSelectedState(et, btn, p);
                } else {
                    Toast.makeText(this, "Nessun piatto trovato", Toast.LENGTH_SHORT).show();
                }
            } else {
                clearDishState(et, btn);
            }
        });
    }

    private void setDishSelectedState(EditText et, ImageButton btn, Piatto piatto) {
        et.setText(piatto.getNomePiatto());
        et.setEnabled(false);
        et.setBackgroundResource(R.drawable.bg_selected_piatto);
        et.setTextColor(ContextCompat.getColor(this, android.R.color.black));
        et.setPadding(30, 20, 30, 20);
        et.setTag(piatto.getId());
        et.setOnClickListener(v -> {
            Intent intent = new Intent(this, PiattoDetailActivity.class);
            intent.putExtra(PiattoDetailActivity.EXTRA_PIATTO_ID, (int)et.getTag());
            startActivity(intent);
        });

        btn.setImageResource(R.drawable.ic_trash_can);
    }

    private void clearDishState(EditText et, ImageButton btn) {
        et.setText("");
        et.setEnabled(true);
        et.setBackgroundResource(android.R.drawable.edit_text);
        et.setTag(null);
        et.setOnClickListener(null);
        btn.setImageResource(R.drawable.ic_magic_wand);
    }

    private void showSuggestions(EditText et, ImageButton btn, ArrayList<Piatto> results) {
        dismissPopup();
        popupWindow = new ListPopupWindow(this);
        popupWindow.setAnchorView(et);
        List<String> names = new ArrayList<>();
        for (Piatto p : results) names.add(p.getNomePiatto());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, names);
        popupWindow.setAdapter(adapter);
        popupWindow.setOnItemClickListener((parent, view, position, id) -> {
            setDishSelectedState(et, btn, results.get(position));
            dismissPopup();
        });
        popupWindow.show();
    }

    private void dismissPopup() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }
}
