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
            if (!destFile.exists()) {
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
                    int id = c.getInt(c.getColumnIndex("ID"));
                    String nome = c.getString(c.getColumnIndex("NOME_PIATTO"));
                    String portata = c.getString(c.getColumnIndex("PORTATA_PIATTO"));
                    String nutrienti = c.getString(c.getColumnIndex("NUTRIENTI_PIATTO"));
                    boolean favorito = false;
                    int favIdx = c.getColumnIndex("FAVORITO");
                    if (favIdx >= 0) favorito = c.getInt(favIdx) == 1;
                    Piatto p = new Piatto(id, nome, portata, nutrienti, false, favorito, null, false);
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
                String nome = c.getString(c.getColumnIndex("NOME_PIATTO"));
                String portata = c.getString(c.getColumnIndex("PORTATA_PIATTO"));
                String nutrienti = c.getString(c.getColumnIndex("NUTRIENTI_PIATTO"));
                boolean favorito = false;
                int favIdx = c.getColumnIndex("FAVORITO");
                if (favIdx >= 0) favorito = c.getInt(favIdx) == 1;
                return new Piatto(id, nome, portata, nutrienti, false, favorito, null, false);
            }
        } finally {
            if (c != null && !c.isClosed()) c.close();
            db.close();
        }
        return null;
    }
}
