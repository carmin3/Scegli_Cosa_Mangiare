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
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CalendarioActivity extends AppCompatActivity {

    private enum ViewMode { DAILY, WEEKLY, MONTHLY }
    private ViewMode currentMode = ViewMode.DAILY;
    private GestureDetector gestureDetector;

    private ScrollView dailyView, weeklyView;
    private LinearLayout monthlyView, weeklyContainer;
    private ImageButton viewSwitchBtn, homeBtn;
    private TextView monthTitleTV;
    private GridView monthGridView;

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

        // UI Components
        dailyView = findViewById(R.id.dailyView);
        weeklyView = findViewById(R.id.weeklyView);
        monthlyView = findViewById(R.id.monthlyView);
        weeklyContainer = findViewById(R.id.weeklyContainer);
        viewSwitchBtn = findViewById(R.id.viewSwitchBtn);
        monthTitleTV = findViewById(R.id.monthTitleTV);
        monthGridView = findViewById(R.id.monthGridView);
        homeBtn = findViewById(R.id.homeBtn);

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

        homeBtn.setOnClickListener(v -> finish());
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

        setupViewSwitch();
        setupGestureDetector();
    }

    private void setupGestureDetector() {
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (e1 == null || e2 == null) return false;
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > 100 && Math.abs(velocityX) > 100) {
                    boolean forward = diffX < 0;
                    switch (currentMode) {
                        case DAILY:
                            changeDate(forward ? 1 : -1);
                            break;
                        case WEEKLY:
                            changeWeek(forward ? 1 : -1);
                            break;
                        case MONTHLY:
                            changeMonth(forward ? 1 : -1);
                            break;
                    }
                    return true;
                }
                return false;
            }
        });

        View.OnTouchListener touchListener = (v, event) -> {
            gestureDetector.onTouchEvent(event);
            return false;
        };

        dailyView.setOnTouchListener(touchListener);
        weeklyView.setOnTouchListener(touchListener);
        monthGridView.setOnTouchListener(touchListener);
        monthlyView.setOnTouchListener(touchListener);
    }

    private void animateViewChange(final View view, final boolean forward, final Runnable updateAction) {
        int outAnim = forward ? R.anim.slide_out_left : R.anim.slide_out_right;
        final int inAnim = forward ? R.anim.slide_in_right : R.anim.slide_in_left;

        Animation out = AnimationUtils.loadAnimation(this, outAnim);
        out.setAnimationListener(new Animation.AnimationListener() {
            @Override public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) {
                updateAction.run();
                view.startAnimation(AnimationUtils.loadAnimation(CalendarioActivity.this, inAnim));
            }
            @Override public void onAnimationRepeat(Animation animation) {}
        });
        view.startAnimation(out);
    }

    private void changeDate(int days) {
        try {
            SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            Date date = dbFormat.parse(selectedDate);
            Calendar cal = Calendar.getInstance();
            if (date != null) cal.setTime(date);
            cal.add(Calendar.DAY_OF_MONTH, days);
            selectedDate = dbFormat.format(cal.getTime());

            animateViewChange(dailyView, days > 0, () -> {
                displayDate();
                loadData();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void changeWeek(int weeks) {
        try {
            SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            Date date = dbFormat.parse(selectedDate);
            Calendar cal = Calendar.getInstance();
            if (date != null) cal.setTime(date);
            cal.add(Calendar.WEEK_OF_YEAR, weeks);
            selectedDate = dbFormat.format(cal.getTime());

            animateViewChange(weeklyView, weeks > 0, this::populateWeeklyView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void changeMonth(int months) {
        try {
            SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            Date date = dbFormat.parse(selectedDate);
            Calendar cal = Calendar.getInstance();
            if (date != null) cal.setTime(date);
            cal.add(Calendar.MONTH, months);
            selectedDate = dbFormat.format(cal.getTime());

            animateViewChange(monthlyView, months > 0, this::populateMonthlyView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupViewSwitch() {
        viewSwitchBtn.setOnClickListener(v -> {
            switch (currentMode) {
                case DAILY:
                    currentMode = ViewMode.WEEKLY;
                    viewSwitchBtn.setImageResource(R.drawable.ic_view_week);
                    showWeeklyView();
                    break;
                case WEEKLY:
                    currentMode = ViewMode.MONTHLY;
                    viewSwitchBtn.setImageResource(R.drawable.ic_calendar_month);
                    showMonthlyView();
                    break;
                case MONTHLY:
                    currentMode = ViewMode.DAILY;
                    viewSwitchBtn.setImageResource(R.drawable.ic_view_day);
                    showDailyView();
                    break;
            }
        });
    }

    private void showDailyView() {
        dailyView.setVisibility(View.VISIBLE);
        weeklyView.setVisibility(View.GONE);
        monthlyView.setVisibility(View.GONE);
        displayDate();
        loadData();
    }

    private void showWeeklyView() {
        dailyView.setVisibility(View.GONE);
        weeklyView.setVisibility(View.VISIBLE);
        monthlyView.setVisibility(View.GONE);
        populateWeeklyView();
    }

    private void populateWeeklyView() {
        weeklyContainer.removeAllViews();
        try {
            SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            Date date = dbFormat.parse(selectedDate);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.DAY_OF_MONTH, -1); // Start from yesterday

            SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE d MMMM", Locale.ITALIAN);

            for (int i = 0; i < 5; i++) {
                final String dateStr = dbFormat.format(cal.getTime());
                View card = getLayoutInflater().inflate(R.layout.item_weekly_day, weeklyContainer, false);

                TextView dateTV = card.findViewById(R.id.weeklyDateTV);
                TextView summaryTV = card.findViewById(R.id.weeklySummaryTV);
                CardView cardView = card.findViewById(R.id.weeklyDayCard);

                String formattedDate = dayFormat.format(cal.getTime());
                formattedDate = formattedDate.substring(0, 1).toUpperCase() + formattedDate.substring(1);

                // Set height based on day
                ViewGroup.LayoutParams params = cardView.getLayoutParams();
                if (i == 1) { // Oggi
                    params.height = (int) (180 * getResources().getDisplayMetrics().density);
                    dateTV.setText(formattedDate + " (Oggi)");
                } else if (i == 0) { // Ieri
                    params.height = (int) (100 * getResources().getDisplayMetrics().density);
                    dateTV.setText(formattedDate);
                } else { // Altri
                    params.height = (int) (130 * getResources().getDisplayMetrics().density);
                    dateTV.setText(formattedDate);
                }
                cardView.setLayoutParams(params);

                // Summary
                PianoPasto piano = dbHelper.getPianoPastoByDate(dateStr);
                if (piano != null) {
                    StringBuilder sb = new StringBuilder();
                    appendDish(sb, piano.getPranzoPrimo());
                    appendDish(sb, piano.getPranzoSecondo());
                    appendDish(sb, piano.getPranzoContorno());
                    appendDish(sb, piano.getPranzoPiattoUnico());
                    appendDish(sb, piano.getCenaPrimo());
                    appendDish(sb, piano.getCenaSecondo());
                    appendDish(sb, piano.getCenaContorno());
                    appendDish(sb, piano.getCenaPiattoUnico());

                    String summary = sb.toString().trim();
                    if (summary.endsWith(",")) summary = summary.substring(0, summary.length() - 1);
                    summaryTV.setText(summary.isEmpty() ? "Pasti non impostati" : summary);
                } else {
                    summaryTV.setText("Pasti non impostati");
                }

                card.setOnClickListener(v -> {
                    selectedDate = dateStr;
                    currentMode = ViewMode.DAILY;
                    viewSwitchBtn.setImageResource(R.drawable.ic_view_day);
                    showDailyView();
                });

                weeklyContainer.addView(card);
                cal.add(Calendar.DAY_OF_MONTH, 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showMonthlyView() {
        dailyView.setVisibility(View.GONE);
        weeklyView.setVisibility(View.GONE);
        monthlyView.setVisibility(View.VISIBLE);
        populateMonthlyView();
    }

    private void populateMonthlyView() {
        try {
            SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            Date date = dbFormat.parse(selectedDate);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);

            String monthStr = new SimpleDateFormat("MMMM yyyy", Locale.ITALIAN).format(cal.getTime());
            monthStr = monthStr.substring(0, 1).toUpperCase() + monthStr.substring(1);
            monthTitleTV.setText(monthStr);

            String monthPrefix = new SimpleDateFormat("yyyy-MM", Locale.US).format(cal.getTime());
            ArrayList<PianoPasto> piani = dbHelper.getPianiPastoForMonth(monthPrefix);
            Map<Integer, Boolean> populatedDays = new HashMap<>();
            for (PianoPasto p : piani) {
                try {
                    int day = Integer.parseInt(p.getData().substring(8));
                    populatedDays.put(day, true);
                } catch (Exception ignored) {}
            }

            monthGridView.setAdapter(new MonthlyAdapter(cal.get(Calendar.MONTH), cal.get(Calendar.YEAR), populatedDays));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class MonthlyAdapter extends BaseAdapter {
        private int month, year, daysInMonth, firstDayOfWeek;
        private Map<Integer, Boolean> populatedDays;

        public MonthlyAdapter(int month, int year, Map<Integer, Boolean> populatedDays) {
            this.month = month;
            this.year = year;
            this.populatedDays = populatedDays;

            Calendar cal = Calendar.getInstance();
            cal.set(year, month, 1);
            this.daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
            this.firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 2; // Lunedì=0 (Calendar.MONDAY is 2)
            if (this.firstDayOfWeek < 0) this.firstDayOfWeek += 7;
        }

        @Override public int getCount() { return 42; }
        @Override public Object getItem(int position) { return null; }
        @Override public long getItemId(int position) { return position; }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_calendar_day, parent, false);
            }

            TextView dayTV = convertView.findViewById(R.id.dayNumberTV);
            int day = position - firstDayOfWeek + 1;

            if (day > 0 && day <= daysInMonth) {
                dayTV.setText(String.valueOf(day));
                dayTV.setVisibility(View.VISIBLE);

                if (populatedDays.containsKey(day)) {
                    dayTV.setBackgroundResource(R.drawable.bg_pill);
                } else {
                    dayTV.setBackgroundResource(R.drawable.rounded_corner_bg);
                }

                convertView.setOnClickListener(v -> {
                    Calendar c = Calendar.getInstance();
                    c.set(year, month, day);
                    selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(c.getTime());
                    currentMode = ViewMode.DAILY;
                    viewSwitchBtn.setImageResource(R.drawable.ic_view_day);
                    showDailyView();
                });
            } else {
                dayTV.setVisibility(View.INVISIBLE);
                convertView.setOnClickListener(null);
            }

            return convertView;
        }
    }

    private void appendDish(StringBuilder sb, String dish) {
        if (dish != null && !dish.isEmpty()) {
            sb.append(dish).append(", ");
        }
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
