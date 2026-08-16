package com.example.sceglicosamangiare;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class DataBaseHelper extends SQLiteOpenHelper {
    public static final String PIATTO_TABLE = "PIATTO_TABLE";
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_NOME_PIATTO = "NOME_PIATTO";
    public static final String COLUMN_PORTATA_PIATTO = "PORTATA_PIATTO";
    public static final String COLUMN_NUTRIENTI_PIATTO = "NUTRIENTI_PIATTO";
    public static final String COLUMN_PERSONALI = "PERSONALI";
    public static final String COLUMN_FAVORITO = "FAVORITO";
    public static final String COLUMN_BASE_ID = "BASE_ID";
    public static final String COLUMN_TOMBSTONE = "TOMBSTONE";

    public DataBaseHelper(@Nullable Context context) {
        super(context, "personal.DB", null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableStatement = "CREATE TABLE " + PIATTO_TABLE + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NOME_PIATTO + " TEXT, " + COLUMN_PORTATA_PIATTO + " TEXT, "
                + COLUMN_NUTRIENTI_PIATTO + " TEXT, " + COLUMN_PERSONALI + " INTEGER DEFAULT 0, " + COLUMN_FAVORITO + " INTEGER DEFAULT 0, "
                + COLUMN_BASE_ID + " INTEGER, " + COLUMN_TOMBSTONE + " INTEGER DEFAULT 0)";

        db.execSQL(createTableStatement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            try {
                db.execSQL("ALTER TABLE " + PIATTO_TABLE + " ADD COLUMN " + COLUMN_FAVORITO + " INTEGER DEFAULT 0");
            } catch (Exception ignored) {}
            try {
                db.execSQL("ALTER TABLE " + PIATTO_TABLE + " ADD COLUMN " + COLUMN_BASE_ID + " INTEGER");
            } catch (Exception ignored) {}
            try {
                db.execSQL("ALTER TABLE " + PIATTO_TABLE + " ADD COLUMN " + COLUMN_TOMBSTONE + " INTEGER DEFAULT 0");
            } catch (Exception ignored) {}
        }
    }

    // low-level personal DB operations
    public long insertPersonal(Piatto piatto, Integer baseId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NOME_PIATTO, piatto.getNomePiatto());
        cv.put(COLUMN_PORTATA_PIATTO, piatto.getPortata());
        cv.put(COLUMN_NUTRIENTI_PIATTO, piatto.getNutrienti());
        cv.put(COLUMN_PERSONALI, 1);
        cv.put(COLUMN_FAVORITO, piatto.getFavorito() != null && piatto.getFavorito() ? 1 : 0);
        if (baseId != null) cv.put(COLUMN_BASE_ID, baseId);
        long id = db.insert(PIATTO_TABLE, null, cv);
        db.close();
        return id;
    }

    public boolean updatePersonalById(int id, Piatto p) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NOME_PIATTO, p.getNomePiatto());
        cv.put(COLUMN_PORTATA_PIATTO, p.getPortata());
        cv.put(COLUMN_NUTRIENTI_PIATTO, p.getNutrienti());
        cv.put(COLUMN_PERSONALI, 1);
        cv.put(COLUMN_FAVORITO, p.getFavorito() != null && p.getFavorito() ? 1 : 0);
        int rows = db.update(PIATTO_TABLE, cv, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return rows > 0;
    }

    public Piatto getPersonalById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String q = "SELECT * FROM " + PIATTO_TABLE + " WHERE " + COLUMN_ID + " = ?";
        Cursor c = db.rawQuery(q, new String[]{String.valueOf(id)});
        try {
            if (c.moveToFirst()) {
                int piattoIDDB = c.getInt(c.getColumnIndex(COLUMN_ID));
                String nome = c.getString(c.getColumnIndex(COLUMN_NOME_PIATTO));
                String portata = c.getString(c.getColumnIndex(COLUMN_PORTATA_PIATTO));
                String nutrienti = c.getString(c.getColumnIndex(COLUMN_NUTRIENTI_PIATTO));
                boolean personale = c.getInt(c.getColumnIndex(COLUMN_PERSONALI)) == 1;
                boolean favorito = false;
                int favIndex = c.getColumnIndex(COLUMN_FAVORITO);
                if (favIndex >= 0) favorito = c.getInt(favIndex) == 1;
                Integer baseId = null;
                int baseIdx = c.getColumnIndex(COLUMN_BASE_ID);
                if (baseIdx >= 0 && !c.isNull(baseIdx)) baseId = c.getInt(baseIdx);
                boolean tomb = false;
                int tombIdx = c.getColumnIndex(COLUMN_TOMBSTONE);
                if (tombIdx >= 0) tomb = c.getInt(tombIdx) == 1;
                return new Piatto(piattoIDDB, nome, portata, nutrienti, personale, favorito, baseId, tomb);
            }
        } finally {
            if (c != null && !c.isClosed()) c.close();
            db.close();
        }
        return null;
    }

    public Piatto getPersonalByBaseId(int baseId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String q = "SELECT * FROM " + PIATTO_TABLE + " WHERE " + COLUMN_BASE_ID + " = ?";
        Cursor c = db.rawQuery(q, new String[]{String.valueOf(baseId)});
        try {
            if (c.moveToFirst()) {
                int piattoIDDB = c.getInt(c.getColumnIndex(COLUMN_ID));
                String nome = c.getString(c.getColumnIndex(COLUMN_NOME_PIATTO));
                String portata = c.getString(c.getColumnIndex(COLUMN_PORTATA_PIATTO));
                String nutrienti = c.getString(c.getColumnIndex(COLUMN_NUTRIENTI_PIATTO));
                boolean personale = c.getInt(c.getColumnIndex(COLUMN_PERSONALI)) == 1;
                boolean favorito = false;
                int favIndex = c.getColumnIndex(COLUMN_FAVORITO);
                if (favIndex >= 0) favorito = c.getInt(favIndex) == 1;
                boolean tomb = false;
                int tombIdx = c.getColumnIndex(COLUMN_TOMBSTONE);
                if (tombIdx >= 0) tomb = c.getInt(tombIdx) == 1;
                return new Piatto(piattoIDDB, nome, portata, nutrienti, personale, favorito, baseId, tomb);
            }
        } finally {
            if (c != null && !c.isClosed()) c.close();
            db.close();
        }
        return null;
    }

    public ArrayList<Piatto> getAllPersonalNonTombstone() {
        ArrayList<Piatto> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String q = "SELECT * FROM " + PIATTO_TABLE + " WHERE " + COLUMN_TOMBSTONE + " = 0";
        Cursor c = db.rawQuery(q, null);
        try {
            if (c.moveToFirst()) {
                do {
                    int piattoIDDB = c.getInt(c.getColumnIndex(COLUMN_ID));
                    String nome = c.getString(c.getColumnIndex(COLUMN_NOME_PIATTO));
                    String portata = c.getString(c.getColumnIndex(COLUMN_PORTATA_PIATTO));
                    String nutrienti = c.getString(c.getColumnIndex(COLUMN_NUTRIENTI_PIATTO));
                    boolean personale = c.getInt(c.getColumnIndex(COLUMN_PERSONALI)) == 1;
                    boolean favorito = false;
                    int favIndex = c.getColumnIndex(COLUMN_FAVORITO);
                    if (favIndex >= 0) favorito = c.getInt(favIndex) == 1;
                    Integer baseId = null;
                    int baseIdx = c.getColumnIndex(COLUMN_BASE_ID);
                    if (baseIdx >= 0 && !c.isNull(baseIdx)) baseId = c.getInt(baseIdx);
                    boolean tomb = false;
                    int tombIdx = c.getColumnIndex(COLUMN_TOMBSTONE);
                    if (tombIdx >= 0) tomb = c.getInt(tombIdx) == 1;
                    Piatto p = new Piatto(piattoIDDB, nome, portata, nutrienti, personale, favorito, baseId, tomb);
                    list.add(p);
                } while (c.moveToNext());
            }
        } finally {
            if (c != null && !c.isClosed()) c.close();
            db.close();
        }
        return list;
    }

    public boolean deletePersonalById(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(PIATTO_TABLE, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return rows > 0;
    }

    public boolean markTombstoneForBase(int baseId) {
        SQLiteDatabase db = this.getWritableDatabase();
        // if exists personal override for baseId, set tombstone
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TOMBSTONE, 1);
        int rows = db.update(PIATTO_TABLE, cv, COLUMN_BASE_ID + " = ?", new String[]{String.valueOf(baseId)});
        if (rows > 0) {
            db.close();
            return true;
        }
        // else insert a tombstone row
        ContentValues ins = new ContentValues();
        ins.put(COLUMN_PERSONALI, 1);
        ins.put(COLUMN_TOMBSTONE, 1);
        ins.put(COLUMN_BASE_ID, baseId);
        long id = db.insert(PIATTO_TABLE, null, ins);
        db.close();
        return id != -1;
    }

    public boolean setFavoritePersonal(int id, boolean value) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_FAVORITO, value ? 1 : 0);
        int rows = db.update(PIATTO_TABLE, cv, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return rows > 0;
    }

    // export personal DB to JSON file (returns number of exported rows or -1 on error)
    public int exportToJsonFile(File outFile) {
        JSONArray arr = new JSONArray();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + PIATTO_TABLE, null);
        try {
            if (c.moveToFirst()) {
                do {
                    JSONObject o = new JSONObject();
                    int id = c.getInt(c.getColumnIndex(COLUMN_ID));
                    o.put("id", id);
                    o.put("nome", c.optString(c.getColumnIndex(COLUMN_NOME_PIATTO)));
                    o.put("portata", c.optString(c.getColumnIndex(COLUMN_PORTATA_PIATTO)));
                    o.put("nutrienti", c.optString(c.getColumnIndex(COLUMN_NUTRIENTI_PIATTO)));
                    o.put("personale", c.getInt(c.getColumnIndex(COLUMN_PERSONALI)) == 1);
                    int favIdx = c.getColumnIndex(COLUMN_FAVORITO);
                    if (favIdx >= 0) o.put("favorito", c.getInt(favIdx) == 1);
                    int baseIdx = c.getColumnIndex(COLUMN_BASE_ID);
                    if (baseIdx >= 0 && !c.isNull(baseIdx)) o.put("base_id", c.getInt(baseIdx));
                    int tombIdx = c.getColumnIndex(COLUMN_TOMBSTONE);
                    if (tombIdx >= 0) o.put("tombstone", c.getInt(tombIdx) == 1);
                    arr.put(o);
                } while (c.moveToNext());
            }
            // write to file
            FileOutputStream fos = new FileOutputStream(outFile);
            fos.write(arr.toString(2).getBytes());
            fos.close();
            return arr.length();
        } catch (Exception e) {
            Log.e("DB_EXPORT", "export error", e);
            return -1;
        } finally {
            if (c != null && !c.isClosed()) c.close();
            db.close();
        }
    }

    // import JSON array merging into personal DB
    public int importFromJsonFile(File inFile) {
        try {
            FileInputStream fis = new FileInputStream(inFile);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line).append('\n');
            br.close();
            JSONArray arr = new JSONArray(sb.toString());
            int processed = 0;
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                String nome = o.optString("nome", "");
                String portata = o.optString("portata", "");
                String nutrienti = o.optString("nutrienti", "");
                boolean favorito = o.optBoolean("favorito", false);
                Integer baseId = null;
                if (o.has("base_id")) baseId = o.optInt("base_id");
                // merge logic: if base_id exists and personal override exists -> update; else insert
                if (baseId != null) {
                    Piatto existing = getPersonalByBaseId(baseId);
                    if (existing != null) {
                        existing.setNomePiatto(nome);
                        existing.setPortata(portata);
                        existing.setNutrienti(nutrienti);
                        existing.setFavorito(favorito);
                        updatePersonalById(existing.getId(), existing);
                        processed++;
                        continue;
                    } else {
                        Piatto newP = new Piatto(-1, nome, portata, nutrienti, true, favorito, baseId, false);
                        long nid = insertPersonal(newP, baseId);
                        if (nid != -1) processed++;
                        continue;
                    }
                }
                // else try by id
                if (o.has("id")) {
                    int id = o.optInt("id", -1);
                    if (id >= 0) {
                        Piatto ex = getPersonalById(id);
                        if (ex != null) {
                            ex.setNomePiatto(nome);
                            ex.setPortata(portata);
                            ex.setNutrienti(nutrienti);
                            ex.setFavorito(favorito);
                            updatePersonalById(id, ex);
                            processed++;
                            continue;
                        }
                    }
                }
                // otherwise insert as new personal
                Piatto newP = new Piatto(-1, nome, portata, nutrienti, true, favorito, null, false);
                long nid = insertPersonal(newP, null);
                if (nid != -1) processed++;
            }
            return processed;
        } catch (Exception e) {
            Log.e("DB_IMPORT", "import error", e);
            return -1;
        }
    }

    // clear personal DB content (delete all rows)
    public boolean clearPersonal() {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(PIATTO_TABLE, null, null);
        db.close();
        return rows >= 0;
    }

}
