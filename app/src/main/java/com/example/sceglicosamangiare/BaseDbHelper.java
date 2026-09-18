package com.example.sceglicosamangiare;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.io.File;
import java.io.FileOutputStream;

import android.database.sqlite.SQLiteException;

import org.json.JSONObject;

import java.util.ArrayList;

public class BaseDbHelper {
    private final Context ctx;
    private final String assetName = "base.db";
    private final File destFile;

    public BaseDbHelper(Context context) {
        this.ctx = context;
        this.destFile = ctx.getDatabasePath("base.db");
        ensureCopied();
    }

    private void ensureCopied() {
        try {
            if (!destFile.getParentFile().exists()) destFile.getParentFile().mkdirs();
            
            boolean schemaOutdated = false;
            if (destFile.exists()) {
                // Check if new columns exist
                SQLiteDatabase db = SQLiteDatabase.openDatabase(destFile.getPath(), null, SQLiteDatabase.OPEN_READONLY);
                Cursor cursor = db.rawQuery("PRAGMA table_info(PIATTO_TABLE)", null);
                boolean hasDominanza = false;
                while (cursor.moveToNext()) {
                    String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                    if ("DOMINANZA_NUTRIZIONALE".equalsIgnoreCase(name)) hasDominanza = true;
                }
                cursor.close();
                db.close();
                if (!hasDominanza) schemaOutdated = true;
                
                // Also check if table is empty
                db = SQLiteDatabase.openDatabase(destFile.getPath(), null, SQLiteDatabase.OPEN_READONLY);
                Cursor countCursor = db.rawQuery("SELECT COUNT(*) FROM PIATTO_TABLE", null);
                if (countCursor.moveToFirst()) {
                    if (countCursor.getInt(0) == 0) schemaOutdated = true;
                }
                countCursor.close();
                db.close();
            }

            if (!destFile.exists() || schemaOutdated) {
                if (schemaOutdated) Log.i("BaseDbHelper", "Schema outdated, re-importing base database...");
                // create DB by executing SQL in assets/base_seed.sql if present
                try {
                    InputStream is = ctx.getAssets().open("base_seed.sql");
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) sb.append(line).append('\n');
                    br.close();
                    String sql = sb.toString();
                    SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(destFile.getPath(), null);
                    String[] parts = sql.split(";\n");
                    for (String part : parts) {
                        String t = part.trim();
                        if (t.isEmpty()) continue;
                        try { db.execSQL(t); } catch (SQLiteException se) { Log.w("BaseDbHelper", "execSQL failed", se); }
                    }
                    db.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private SQLiteDatabase openRead() {
        return SQLiteDatabase.openDatabase(destFile.getPath(), null, SQLiteDatabase.OPEN_READONLY);
    }

    public ArrayList<Piatto> getAllBasePiatti() {
        ArrayList<Piatto> list = new ArrayList<>();
        SQLiteDatabase db = openRead();
        Cursor c = db.rawQuery("SELECT * FROM PIATTO_TABLE", null);
        try {
            if (c.moveToFirst()) {
                do {
                    int id = c.getInt(c.getColumnIndexOrThrow("ID"));
                    String nome = c.getString(c.getColumnIndexOrThrow("NOME_PIATTO"));
                    String portata = c.getString(c.getColumnIndexOrThrow("PORTATA_PIATTO"));
                    String nutrienti = c.getString(c.getColumnIndexOrThrow("NUTRIENTI_PIATTO"));
                    String dominanza = "";
                    int domIdx = c.getColumnIndex("DOMINANZA_NUTRIZIONALE");
                    if (domIdx >= 0) dominanza = c.getString(domIdx);
                    String profilo = "";
                    int profIdx = c.getColumnIndex("PROFILO_GUSTATIVO");
                    if (profIdx >= 0) profilo = c.getString(profIdx);
                    boolean favorito = false;
                    int favIdx = c.getColumnIndex("FAVORITO");
                    if (favIdx >= 0) favorito = c.getInt(favIdx) == 1;
                    Piatto p = new Piatto(id, nome, portata, nutrienti, dominanza, profilo, false, favorito, null, false);
                    list.add(p);
                } while (c.moveToNext());
            }
        } finally {
            if (c != null && !c.isClosed()) c.close();
            db.close();
        }
        return list;
    }

    public Piatto getBaseById(int id) {
        SQLiteDatabase db = openRead();
        Cursor c = db.rawQuery("SELECT * FROM PIATTO_TABLE WHERE ID = ?", new String[]{String.valueOf(id)});
        try {
            if (c.moveToFirst()) {
                String nome = c.getString(c.getColumnIndexOrThrow("NOME_PIATTO"));
                String portata = c.getString(c.getColumnIndexOrThrow("PORTATA_PIATTO"));
                String nutrienti = c.getString(c.getColumnIndexOrThrow("NUTRIENTI_PIATTO"));
                String dominanza = "";
                int domIdx = c.getColumnIndex("DOMINANZA_NUTRIZIONALE");
                if (domIdx >= 0) dominanza = c.getString(domIdx);
                String profilo = "";
                int profIdx = c.getColumnIndex("PROFILO_GUSTATIVO");
                if (profIdx >= 0) profilo = c.getString(profIdx);
                boolean favorito = false;
                int favIdx = c.getColumnIndex("FAVORITO");
                if (favIdx >= 0) favorito = c.getInt(favIdx) == 1;
                return new Piatto(id, nome, portata, nutrienti, dominanza, profilo, false, favorito, null, false);
            }
        } finally {
            if (c != null && !c.isClosed()) c.close();
            db.close();
        }
        return null;
    }
}
