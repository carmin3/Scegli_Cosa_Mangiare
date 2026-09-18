package com.example.sceglicosamangiare;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

public class WeightManager {
    private static final String PREFS_NAME = "DietaBilanciataPrefs";
    
    public static final String KEY_CARNE_ROSSA = "Carne Rossa";
    public static final String KEY_CARNE_BIANCA = "Carne Bianca";
    public static final String KEY_PESCE = "Pesce";
    public static final String KEY_VEGETARIANO = "Vegetariano";

    // Valori di default basati sull'app attuale
    private static final float DEFAULT_ROSSA = 5.0f;
    private static final float DEFAULT_BIANCA = 20.0f;
    private static final float DEFAULT_PESCE = 45.0f;
    private static final float DEFAULT_VEG = 20.0f;

    private final SharedPreferences prefs;

    public WeightManager(Context context) {
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public Map<String, Float> getWeights() {
        Map<String, Float> weights = new HashMap<>();
        weights.put(KEY_CARNE_ROSSA, prefs.getFloat(KEY_CARNE_ROSSA, DEFAULT_ROSSA));
        weights.put(KEY_CARNE_BIANCA, prefs.getFloat(KEY_CARNE_BIANCA, DEFAULT_BIANCA));
        weights.put(KEY_PESCE, prefs.getFloat(KEY_PESCE, DEFAULT_PESCE));
        weights.put(KEY_VEGETARIANO, prefs.getFloat(KEY_VEGETARIANO, DEFAULT_VEG));
        return weights;
    }

    public void saveWeights(Map<String, Float> weights) {
        SharedPreferences.Editor editor = prefs.edit();
        for (Map.Entry<String, Float> entry : weights.entrySet()) {
            editor.putFloat(entry.getKey(), entry.getValue());
        }
        editor.apply();
    }

    public void resetToDefault() {
        prefs.edit().clear().apply();
    }
    
    public float getWeight(String key) {
        switch (key) {
            case KEY_CARNE_ROSSA: return prefs.getFloat(KEY_CARNE_ROSSA, DEFAULT_ROSSA);
            case KEY_CARNE_BIANCA: return prefs.getFloat(KEY_CARNE_BIANCA, DEFAULT_BIANCA);
            case KEY_PESCE: return prefs.getFloat(KEY_PESCE, DEFAULT_PESCE);
            case KEY_VEGETARIANO: return prefs.getFloat(KEY_VEGETARIANO, DEFAULT_VEG);
            default: return 0f;
        }
    }
}
