package com.example.sceglicosamangiare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import java.util.List;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.TextView;
import androidx.cardview.widget.CardView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Calendar currentCalendarDate = Calendar.getInstance();
    private DataBaseHelper dbHelper;
    private TextView calendarDateTV, pranzoPastiTV, cenaPastiTV;
    private SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE d MMMM", Locale.ITALIAN);
    private SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DataBaseHelper(this);

        calendarDateTV = findViewById(R.id.calendarDateTV);
        pranzoPastiTV = findViewById(R.id.pranzoPastiTV);
        cenaPastiTV = findViewById(R.id.cenaPastiTV);
        CardView calendarCard = findViewById(R.id.calendarCard);

        updateCalendarDisplay();

        if (calendarCard != null) {
            calendarCard.setOnClickListener(v -> {
                Intent i = new Intent(MainActivity.this, CalendarioActivity.class);
                i.putExtra("selectedDate", dbFormat.format(currentCalendarDate.getTime()));
                startActivity(i);
            });
        }

        setupSwipeDetector(calendarCard);

        Button ProponiPiattoBtn = findViewById(R.id.ProponiPiattoBtn);
        if (ProponiPiattoBtn != null) {
            ProponiPiattoBtn.setOnClickListener(v -> {
                Intent ProponiPiattoIntent = new Intent(MainActivity.this, PiattoPropostoActivity.class);
                startActivity(ProponiPiattoIntent);
            });
        }

        ImageButton ListaBtn = findViewById(R.id.ListaBtn);
        if (ListaBtn != null) {
            ListaBtn.setOnClickListener(v -> {
                Intent ListaPiattiIntent = new Intent(MainActivity.this, ListaPiattiActivity.class);
                startActivity(ListaPiattiIntent);
            });
        }

        ImageButton settingsBtn = findViewById(R.id.settingsBtn);
        if (settingsBtn != null) {
            settingsBtn.setOnClickListener(v -> {
                Intent i = new Intent(MainActivity.this, ImpostazioniActivity.class);
                startActivity(i);
            });
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCalendarDisplay();
    }

    private void updateCalendarDisplay() {
        String dateStr = dayFormat.format(currentCalendarDate.getTime());
        // Capitalize first letter
        dateStr = dateStr.substring(0, 1).toUpperCase() + dateStr.substring(1);
        calendarDateTV.setText(dateStr);

        String dbDate = dbFormat.format(currentCalendarDate.getTime());
        PianoPasto piano = dbHelper.getPianoPastoByDate(dbDate);

        if (piano != null) {
            pranzoPastiTV.setText(formatPasti(piano.getPranzoPrimo(), piano.getPranzoSecondo(), piano.getPranzoContorno(), piano.getPranzoPiattoUnico()));
            cenaPastiTV.setText(formatPasti(piano.getCenaPrimo(), piano.getCenaSecondo(), piano.getCenaContorno(), piano.getCenaPiattoUnico()));
        } else {
            pranzoPastiTV.setText("");
            cenaPastiTV.setText("");
        }
    }

    private String formatPasti(String p1, String p2, String p3, String p4) {
        StringBuilder sb = new StringBuilder();
        if (p1 != null && !p1.isEmpty()) sb.append("• ").append(p1).append("\n");
        if (p2 != null && !p2.isEmpty()) sb.append("• ").append(p2).append("\n");
        if (p3 != null && !p3.isEmpty()) sb.append("• ").append(p3).append("\n");
        if (p4 != null && !p4.isEmpty()) sb.append("• ").append(p4);
        return sb.toString().trim();
    }

    private void setupSwipeDetector(android.view.View view) {
        GestureDetector gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (e1 == null || e2 == null) return false;
                if (Math.abs(e1.getX() - e2.getX()) > 100 && Math.abs(velocityX) > 100) {
                    if (e1.getX() > e2.getX()) {
                        // Swipe left -> Next day
                        currentCalendarDate.add(Calendar.DAY_OF_YEAR, 1);
                    } else {
                        // Swipe right -> Previous day
                        currentCalendarDate.add(Calendar.DAY_OF_YEAR, -1);
                    }
                    updateCalendarDisplay();
                    return true;
                }
                return false;
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                view.performClick();
                return true;
            }
        });

        view.setOnTouchListener((v, event) -> {
            gestureDetector.onTouchEvent(event);
            return true;
        });
    }
}
