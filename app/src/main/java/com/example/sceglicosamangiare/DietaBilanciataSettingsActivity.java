package com.example.sceglicosamangiare;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

public class DietaBilanciataSettingsActivity extends AppCompatActivity {

    private SeekBar seekBarRossa, seekBarBianca, seekBarPesce, seekBarVeg;
    private TextView tvValueRossa, tvValueBianca, tvValuePesce, tvValueVeg;
    private Button btnApplica, btnRipristina;
    private WeightManager weightManager;
    private boolean isModified = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dieta_bilanciata_settings);

        weightManager = new WeightManager(this);

        initViews();
        loadWeights();
        setupListeners();
    }

    private void initViews() {
        seekBarRossa = findViewById(R.id.seekBarRossa);
        seekBarBianca = findViewById(R.id.seekBarBianca);
        seekBarPesce = findViewById(R.id.seekBarPesce);
        seekBarVeg = findViewById(R.id.seekBarVeg);

        tvValueRossa = findViewById(R.id.tvValueRossa);
        tvValueBianca = findViewById(R.id.tvValueBianca);
        tvValuePesce = findViewById(R.id.tvValuePesce);
        tvValueVeg = findViewById(R.id.tvValueVeg);

        btnApplica = findViewById(R.id.btnApplica);
        btnRipristina = findViewById(R.id.btnRipristina);

        ImageButton homeBtn = findViewById(R.id.homeBtn);
        homeBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void loadWeights() {
        Map<String, Float> weights = weightManager.getWeights();

        updateSeekBar(seekBarRossa, tvValueRossa, weights.get(WeightManager.KEY_CARNE_ROSSA));
        updateSeekBar(seekBarBianca, tvValueBianca, weights.get(WeightManager.KEY_CARNE_BIANCA));
        updateSeekBar(seekBarPesce, tvValuePesce, weights.get(WeightManager.KEY_PESCE));
        updateSeekBar(seekBarVeg, tvValueVeg, weights.get(WeightManager.KEY_VEGETARIANO));
        
        isModified = false;
        btnApplica.setVisibility(View.GONE);
    }

    private void updateSeekBar(SeekBar seekBar, TextView textView, Float value) {
        int intValue = value != null ? Math.round(value) : 0;
        seekBar.setProgress(intValue);
        textView.setText(String.valueOf(intValue));
    }

    private void setupListeners() {
        SeekBar.OnSeekBarChangeListener listener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    if (seekBar == seekBarRossa) tvValueRossa.setText(String.valueOf(progress));
                    else if (seekBar == seekBarBianca) tvValueBianca.setText(String.valueOf(progress));
                    else if (seekBar == seekBarPesce) tvValuePesce.setText(String.valueOf(progress));
                    else if (seekBar == seekBarVeg) tvValueVeg.setText(String.valueOf(progress));
                    
                    isModified = true;
                    btnApplica.setVisibility(View.VISIBLE);
                }
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        };

        seekBarRossa.setOnSeekBarChangeListener(listener);
        seekBarBianca.setOnSeekBarChangeListener(listener);
        seekBarPesce.setOnSeekBarChangeListener(listener);
        seekBarVeg.setOnSeekBarChangeListener(listener);

        btnApplica.setOnClickListener(v -> {
            Map<String, Float> weights = new HashMap<>();
            weights.put(WeightManager.KEY_CARNE_ROSSA, (float) seekBarRossa.getProgress());
            weights.put(WeightManager.KEY_CARNE_BIANCA, (float) seekBarBianca.getProgress());
            weights.put(WeightManager.KEY_PESCE, (float) seekBarPesce.getProgress());
            weights.put(WeightManager.KEY_VEGETARIANO, (float) seekBarVeg.getProgress());

            weightManager.saveWeights(weights);
            isModified = false;
            btnApplica.setVisibility(View.GONE);
            Toast.makeText(this, "Impostazioni salvate", Toast.LENGTH_SHORT).show();
        });

        btnRipristina.setOnClickListener(v -> {
            weightManager.resetToDefault();
            loadWeights();
            Toast.makeText(this, "Valori ripristinati", Toast.LENGTH_SHORT).show();
        });
    }
}
