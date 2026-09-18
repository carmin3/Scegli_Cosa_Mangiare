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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

    private ScrollView dailyView;
    private RecyclerView weeklyView;
    private LinearLayout monthlyView;
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
    private String[] proteine = {"Dieta Bilanciata", "Carne Rossa", "Carne Bianca", "Pesce", "Proteine Vegetali", "Neutro"};

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
        viewSwitchBtn = findViewById(R.id.viewSwitchBtn);
        monthTitleTV = findViewById(R.id.monthTitleTV);
        monthGridView = findViewById(R.id.monthGridView);
        homeBtn = findViewById(R.id.homeBtn);

        weeklyView.setLayoutManager(new LinearLayoutManager(this));

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
        if (weeklyView.getAdapter() == null) return;
        WeeklyAdapter adapter = (WeeklyAdapter) weeklyView.getAdapter();
        
        try {
            SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            Date date = dbFormat.parse(selectedDate);
            Calendar cal = Calendar.getInstance();
            if (date != null) cal.setTime(date);
            cal.add(Calendar.WEEK_OF_YEAR, weeks);
            selectedDate = dbFormat.format(cal.getTime());

            int targetPos = adapter.getPositionForDate(selectedDate);
            weeklyView.smoothScrollToPosition(targetPos);
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
        if (weeklyView.getAdapter() == null) {
            weeklyView.setAdapter(new WeeklyAdapter());
        }
        WeeklyAdapter adapter = (WeeklyAdapter) weeklyView.getAdapter();
        weeklyView.scrollToPosition(adapter.getPositionForDate(selectedDate));
    }

    private class WeeklyAdapter extends RecyclerView.Adapter<WeeklyAdapter.ViewHolder> {
        private final Calendar baseCalendar;
        private final SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        private final SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE d MMMM", Locale.ITALIAN);
        private static final int CENTER_POSITION = 500000;

        public WeeklyAdapter() {
            baseCalendar = Calendar.getInstance();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_weekly_day, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Calendar cal = (Calendar) baseCalendar.clone();
            cal.add(Calendar.DAY_OF_YEAR, position - CENTER_POSITION);

            String dateStr = dbFormat.format(cal.getTime());
            String formattedDate = dayFormat.format(cal.getTime());
            formattedDate = formattedDate.substring(0, 1).toUpperCase() + formattedDate.substring(1);

            Calendar today = Calendar.getInstance();
            boolean isToday = cal.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                             cal.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR);

            holder.dateTV.setText(isToday ? formattedDate + " (Oggi)" : formattedDate);

            ViewGroup.LayoutParams params = holder.cardView.getLayoutParams();
            if (isToday) {
                params.height = (int) (180 * getResources().getDisplayMetrics().density);
            } else {
                params.height = (int) (130 * getResources().getDisplayMetrics().density);
            }
            holder.cardView.setLayoutParams(params);

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
                holder.summaryTV.setText(summary.isEmpty() ? "Pasti non impostati" : summary);
            } else {
                holder.summaryTV.setText("Pasti non impostati");
            }

            holder.itemView.setOnClickListener(v -> {
                selectedDate = dateStr;
                currentMode = ViewMode.DAILY;
                viewSwitchBtn.setImageResource(R.drawable.ic_view_day);
                showDailyView();
            });
        }

        @Override
        public int getItemCount() {
            return CENTER_POSITION * 2;
        }

        public int getPositionForDate(String dateStr) {
            try {
                SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                Date targetDate = parser.parse(dateStr);
                
                Calendar base = Calendar.getInstance();
                base.setTime(baseCalendar.getTime());
                base.set(Calendar.HOUR_OF_DAY, 0);
                base.set(Calendar.MINUTE, 0);
                base.set(Calendar.SECOND, 0);
                base.set(Calendar.MILLISECOND, 0);

                Calendar target = Calendar.getInstance();
                target.setTime(targetDate);
                target.set(Calendar.HOUR_OF_DAY, 0);
                target.set(Calendar.MINUTE, 0);
                target.set(Calendar.SECOND, 0);
                target.set(Calendar.MILLISECOND, 0);

                long diff = target.getTimeInMillis() - base.getTimeInMillis();
                int days = (int) Math.round(diff / (24.0 * 60 * 60 * 1000));
                return CENTER_POSITION + days;
            } catch (Exception e) {
                return CENTER_POSITION;
            }
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView dateTV, summaryTV;
            CardView cardView;

            ViewHolder(View view) {
                super(view);
                dateTV = view.findViewById(R.id.weeklyDateTV);
                summaryTV = view.findViewById(R.id.weeklySummaryTV);
                cardView = view.findViewById(R.id.weeklyDayCard);
            }
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
        } else {
            clearDishState(etPranzoPrimo, btnActionPPrimo);
            clearDishState(etPranzoSecondo, btnActionPSecondo);
            clearDishState(etPranzoContorno, btnActionPContorno);
            clearDishState(etPranzoPiattoUnico, btnActionPPiattoUnico);
            clearDishState(etCenaPrimo, btnActionCPrimo);
            clearDishState(etCenaSecondo, btnActionCSecondo);
            clearDishState(etCenaContorno, btnActionCContorno);
            clearDishState(etCenaPiattoUnico, btnActionCPiattoUnico);

            spinnerProtPranzo.setSelection(0);
            spinnerProtCena.setSelection(0);
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
        String valToSelect = value;
        if (value != null && value.equalsIgnoreCase("Casuale")) {
            valToSelect = "Dieta Bilanciata";
        }
        for (int i = 0; i < proteine.length; i++) {
            if (proteine[i].equalsIgnoreCase(valToSelect)) {
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
        if (proteinaPranzo.equalsIgnoreCase("Dieta Bilanciata")) {
            proteinaPranzo = getWeightedRandomProteina();
            setSpinnerSelection(spinnerProtPranzo, proteinaPranzo);
        }

        Pasto pastoPranzo = repository.generaPasto(proteinaPranzo);
        if (pastoPranzo.getPrimo() != null) setDishSelectedState(etPranzoPrimo, btnActionPPrimo, pastoPranzo.getPrimo());
        if (pastoPranzo.getSecondo() != null) setDishSelectedState(etPranzoSecondo, btnActionPSecondo, pastoPranzo.getSecondo());
        if (pastoPranzo.getPiattoUnico() != null) setDishSelectedState(etPranzoPiattoUnico, btnActionPPiattoUnico, pastoPranzo.getPiattoUnico());
        if (pastoPranzo.getContorno() != null) setDishSelectedState(etPranzoContorno, btnActionPContorno, pastoPranzo.getContorno());

        // --- CENA ---
        String proteinaCena = spinnerProtCena.getSelectedItem().toString();
        if (proteinaCena.equalsIgnoreCase("Dieta Bilanciata")) {
            proteinaCena = getWeightedRandomProteina();
            setSpinnerSelection(spinnerProtCena, proteinaCena);
        }

        Pasto pastoCena = repository.generaPasto(proteinaCena);
        if (pastoCena.getPrimo() != null) setDishSelectedState(etCenaPrimo, btnActionCPrimo, pastoCena.getPrimo());
        if (pastoCena.getSecondo() != null) setDishSelectedState(etCenaSecondo, btnActionCSecondo, pastoCena.getSecondo());
        if (pastoCena.getPiattoUnico() != null) setDishSelectedState(etCenaPiattoUnico, btnActionCPiattoUnico, pastoCena.getPiattoUnico());
        if (pastoCena.getContorno() != null) setDishSelectedState(etCenaContorno, btnActionCContorno, pastoCena.getContorno());

        Toast.makeText(this, "Menù generato!", Toast.LENGTH_SHORT).show();
    }

    private String getWeightedRandomProteina() {
        WeightManager weightManager = new WeightManager(this);
        SceltaCasualeTipoProteina<String> itemDrops = new SceltaCasualeTipoProteina<>();
        
        itemDrops.addEntry("Carne Rossa", weightManager.getWeight(WeightManager.KEY_CARNE_ROSSA));
        itemDrops.addEntry("Carne Bianca", weightManager.getWeight(WeightManager.KEY_CARNE_BIANCA));
        itemDrops.addEntry("Pesce", weightManager.getWeight(WeightManager.KEY_PESCE));
        itemDrops.addEntry("Proteine Vegetali", weightManager.getWeight(WeightManager.KEY_VEG));
        itemDrops.addEntry("Neutro", weightManager.getWeight(WeightManager.KEY_NEUTRO));
        
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
                if (proteina.equalsIgnoreCase("Dieta Bilanciata")) {
                    // For single refresh we can't easily guess the weighted nutrient intended without picking it
                    // Let's just pick one if not set
                    proteina = getWeightedRandomProteina();
                }

                Pasto p = repository.generaPasto(proteina);
                Piatto scelto = null;
                if (portata.equalsIgnoreCase("Primo")) scelto = p.getPrimo();
                else if (portata.equalsIgnoreCase("Secondo")) scelto = p.getSecondo();
                else if (portata.equalsIgnoreCase("Contorno")) scelto = p.getContorno();
                else if (portata.equalsIgnoreCase("Piatto Unico")) scelto = p.getPiattoUnico();

                if (scelto != null) {
                    setDishSelectedState(et, btn, scelto);
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
