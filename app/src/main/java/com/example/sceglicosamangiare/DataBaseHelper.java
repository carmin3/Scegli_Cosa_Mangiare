package com.example.sceglicosamangiare;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

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

}
