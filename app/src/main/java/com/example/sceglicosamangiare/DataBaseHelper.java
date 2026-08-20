package com.example.sceglicosamangiare;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
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
        super(context, "personal.DB", null, 3);
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
            if (c != null && c.moveToFirst()) {
                return getPiattoFromCursor(c);
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
            if (c != null && c.moveToFirst()) {
                return getPiattoFromCursor(c);
            }
        } finally {
            if (c != null && !c.isClosed()) c.close();
            db.close();
        }
        return null;
    }

    private Piatto getPiattoFromCursor(Cursor c) {
        int idIdx = c.getColumnIndex(COLUMN_ID);
        int nomeIdx = c.getColumnIndex(COLUMN_NOME_PIATTO);
        int portataIdx = c.getColumnIndex(COLUMN_PORTATA_PIATTO);
        int nutrientiIdx = c.getColumnIndex(COLUMN_NUTRIENTI_PIATTO);
        int persIdx = c.getColumnIndex(COLUMN_PERSONALI);
        int favIdx = c.getColumnIndex(COLUMN_FAVORITO);
        int baseIdx = c.getColumnIndex(COLUMN_BASE_ID);
        int tombIdx = c.getColumnIndex(COLUMN_TOMBSTONE);

        int id = (idIdx != -1) ? c.getInt(idIdx) : -1;
        String nome = (nomeIdx != -1) ? c.getString(nomeIdx) : "";
        String portata = (portataIdx != -1) ? c.getString(portataIdx) : "";
        String nutrienti = (nutrientiIdx != -1) ? c.getString(nutrientiIdx) : "";
        boolean personale = (persIdx != -1) && c.getInt(persIdx) == 1;
        boolean favorito = (favIdx != -1) && c.getInt(favIdx) == 1;
        Integer baseId = (baseIdx != -1 && !c.isNull(baseIdx)) ? c.getInt(baseIdx) : null;
        boolean tombstone = (tombIdx != -1) && c.getInt(tombIdx) == 1;

        return new Piatto(id, nome, portata, nutrienti, personale, favorito, baseId, tombstone);
    }

    public ArrayList<Piatto> getAllPersonalNonTombstone() {
        ArrayList<Piatto> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String q = "SELECT * FROM " + PIATTO_TABLE + " WHERE " + COLUMN_TOMBSTONE + " = 0";
        Cursor c = db.rawQuery(q, null);
        try {
            if (c != null && c.moveToFirst()) {
                do {
                    list.add(getPiattoFromCursor(c));
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

    // export personal DB to JSON file
    public int exportToJsonFile(File outFile) {
        try (FileOutputStream fos = new FileOutputStream(outFile)) {
            return exportToOutputStream(fos);
        } catch (Exception e) {
            Log.e("DB_EXPORT", "file export error", e);
            return -1;
        }
    }

    public int exportToOutputStream(OutputStream os) {
        JSONArray arr = new JSONArray();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + PIATTO_TABLE, null);
        try {
            if (c != null && c.moveToFirst()) {
                int idIdx = c.getColumnIndex(COLUMN_ID);
                int nomeIdx = c.getColumnIndex(COLUMN_NOME_PIATTO);
                int portataIdx = c.getColumnIndex(COLUMN_PORTATA_PIATTO);
                int nutrientiIdx = c.getColumnIndex(COLUMN_NUTRIENTI_PIATTO);
                int persIdx = c.getColumnIndex(COLUMN_PERSONALI);
                int favIdx = c.getColumnIndex(COLUMN_FAVORITO);
                int baseIdx = c.getColumnIndex(COLUMN_BASE_ID);
                int tombIdx = c.getColumnIndex(COLUMN_TOMBSTONE);

                do {
                    JSONObject o = new JSONObject();
                    o.put("id", (idIdx != -1) ? c.getInt(idIdx) : -1);
                    o.put("nome", (nomeIdx != -1 && !c.isNull(nomeIdx)) ? c.getString(nomeIdx) : "");
                    o.put("portata", (portataIdx != -1 && !c.isNull(portataIdx)) ? c.getString(portataIdx) : "");
                    o.put("nutrienti", (nutrientiIdx != -1 && !c.isNull(nutrientiIdx)) ? c.getString(nutrientiIdx) : "");
                    o.put("personale", (persIdx != -1) && c.getInt(persIdx) == 1);
                    if (favIdx != -1) o.put("favorito", c.getInt(favIdx) == 1);
                    if (baseIdx != -1 && !c.isNull(baseIdx)) o.put("base_id", c.getInt(baseIdx));
                    if (tombIdx != -1) o.put("tombstone", c.getInt(tombIdx) == 1);
                    arr.put(o);
                } while (c.moveToNext());
            }
            os.write(arr.toString(2).getBytes());
            return arr.length();
        } catch (Exception e) {
            Log.e("DB_EXPORT", "stream export error", e);
            return -1;
        } finally {
            if (c != null && !c.isClosed()) c.close();
            db.close();
        }
    }

    // import JSON array merging into personal DB
    public int importFromJsonFile(File inFile) {
        try (FileInputStream fis = new FileInputStream(inFile)) {
            return importFromInputStream(fis);
        } catch (Exception e) {
            Log.e("DB_IMPORT", "file import error", e);
            return -1;
        }
    }

    public int importFromInputStream(InputStream is) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append('\n');
            }
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

                // merge logic
                if (baseId != null) {
                    Piatto existing = getPersonalByBaseId(baseId);
                    if (existing != null) {
                        existing.setNomePiatto(nome);
                        existing.setPortata(portata);
                        existing.setNutrienti(nutrienti);
                        existing.setFavorito(favorito);
                        updatePersonalById(existing.getId(), existing);
                    } else {
                        Piatto newP = new Piatto(-1, nome, portata, nutrienti, true, favorito, baseId, false);
                        insertPersonal(newP, baseId);
                    }
                    processed++;
                } else if (o.has("id")) {
                    int id = o.optInt("id", -1);
                    Piatto ex = (id >= 0) ? getPersonalById(id) : null;
                    if (ex != null) {
                        ex.setNomePiatto(nome);
                        ex.setPortata(portata);
                        ex.setNutrienti(nutrienti);
                        ex.setFavorito(favorito);
                        updatePersonalById(id, ex);
                    } else {
                        Piatto newP = new Piatto(-1, nome, portata, nutrienti, true, favorito, null, false);
                        insertPersonal(newP, null);
                    }
                    processed++;
                } else {
                    Piatto newP = new Piatto(-1, nome, portata, nutrienti, true, favorito, null, false);
                    long nid = insertPersonal(newP, null);
                    if (nid != -1) processed++;
                }
            }
            return processed;
        } catch (Exception e) {
            Log.e("DB_IMPORT", "stream import error", e);
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
