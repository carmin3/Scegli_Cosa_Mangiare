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


    public DataBaseHelper(@Nullable Context context) {
        super(context, "customer.DB", null, 2); // bumped DB version to 2
    }

    //onCreate viene eseguito solo ed esclusivamente quando viene generato la prima volta il database.
    //bisogna quindi inserirci semplicemente le informazioni per crearlo
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableStatement = "CREATE TABLE " + PIATTO_TABLE + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NOME_PIATTO + " TEXT, " + COLUMN_PORTATA_PIATTO + " TEXT, "
                + COLUMN_NUTRIENTI_PIATTO + " TEXT, " + COLUMN_PERSONALI + " INTEGER DEFAULT 0, " + COLUMN_FAVORITO + " INTEGER DEFAULT 0)";

        db.execSQL(createTableStatement);


    }

     // gestisce migrazioni dal DB precedente
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // aggiungiamo la colonna FAVORITO se non esiste
            try {
                db.execSQL("ALTER TABLE " + PIATTO_TABLE + " ADD COLUMN " + COLUMN_FAVORITO + " INTEGER DEFAULT 0");
            } catch (Exception e) {
                // ignoriamo se la colonna esiste già o se fallisce
                e.printStackTrace();
            }
        }
    }

    public boolean addOne(Piatto piatto){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_NOME_PIATTO, piatto.getNomePiatto());
        cv.put(COLUMN_PORTATA_PIATTO, piatto.getPortata());
        cv.put(COLUMN_NUTRIENTI_PIATTO, piatto.getNutrienti());
        // store boolean as integer 1/0
        cv.put(COLUMN_PERSONALI, piatto.getPersonale() != null && piatto.getPersonale() ? 1 : 0);
        cv.put(COLUMN_FAVORITO, piatto.getFavorito() != null && piatto.getFavorito() ? 1 : 0);

        long insert = db.insert(PIATTO_TABLE, null, cv);
        db.close();
        return insert != -1;
    }

    public boolean existsByNameAndPortata(String nome, String portata, int excludeId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_ID + " FROM " + PIATTO_TABLE + " WHERE LOWER(" + COLUMN_NOME_PIATTO + ") = ? AND LOWER(" + COLUMN_PORTATA_PIATTO + ") = ?";
        String[] args;
        if (excludeId >= 0) {
            query += " AND " + COLUMN_ID + " != ?";
            args = new String[]{nome.toLowerCase().trim(), portata.toLowerCase().trim(), String.valueOf(excludeId)};
        } else {
            args = new String[]{nome.toLowerCase().trim(), portata.toLowerCase().trim()};
        }
        Cursor cursor = db.rawQuery(query, args);
        boolean exists = false;
        try {
            exists = cursor.moveToFirst();
        } finally {
            if (cursor != null && !cursor.isClosed()) cursor.close();
            db.close();
        }
        return exists;
    }

    public Piatto getById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + PIATTO_TABLE + " WHERE " + COLUMN_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(id)});
        try {
            if (cursor.moveToFirst()) {
                int piattoIDDB = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                String nomePiattoDB = cursor.getString(cursor.getColumnIndex(COLUMN_NOME_PIATTO));
                String portataPiattoDB = cursor.getString(cursor.getColumnIndex(COLUMN_PORTATA_PIATTO));
                String nutrientiPiattoDB = cursor.getString(cursor.getColumnIndex(COLUMN_NUTRIENTI_PIATTO));
                boolean personaliDB = cursor.getInt(cursor.getColumnIndex(COLUMN_PERSONALI)) == 1;
                boolean favoritoDB = false;
                int favIndex = cursor.getColumnIndex(COLUMN_FAVORITO);
                if (favIndex >= 0) {
                    favoritoDB = cursor.getInt(favIndex) == 1;
                }
                Piatto p = new Piatto(piattoIDDB, nomePiattoDB, portataPiattoDB, nutrientiPiattoDB, personaliDB, favoritoDB);
                return p;
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) cursor.close();
            db.close();
        }
        return null;
    }

    public boolean updateOne(Piatto p) {
        if (p == null) return false;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NOME_PIATTO, p.getNomePiatto());
        cv.put(COLUMN_PORTATA_PIATTO, p.getPortata());
        cv.put(COLUMN_NUTRIENTI_PIATTO, p.getNutrienti());
        cv.put(COLUMN_PERSONALI, p.getPersonale() != null && p.getPersonale() ? 1 : 0);
        cv.put(COLUMN_FAVORITO, p.getFavorito() != null && p.getFavorito() ? 1 : 0);

        int rows = db.update(PIATTO_TABLE, cv, COLUMN_ID + " = ?", new String[]{String.valueOf(p.getId())});
        db.close();
        return rows > 0;
    }

    public boolean deleteOne(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(PIATTO_TABLE, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return rows > 0;
    }

    public boolean setFavorite(int id, boolean value) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_FAVORITO, value ? 1 : 0);
        int rows = db.update(PIATTO_TABLE, cv, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return rows > 0;
    }

    public ArrayList<Piatto> getAllData() {
        ArrayList<Piatto> listaPiatti = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String queryString = "SELECT * FROM " + PIATTO_TABLE;
        Cursor cursor = db.rawQuery(queryString, null);

        try {
            if (cursor.moveToFirst()) {
                // crea un loop su tutte le righe della tabella e vai a scrivere queste info in una returnList
                do {
                    int piattoIDDB = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                    String nomePiattoDB = cursor.getString(cursor.getColumnIndex(COLUMN_NOME_PIATTO));
                    String portataPiattoDB = cursor.getString(cursor.getColumnIndex(COLUMN_PORTATA_PIATTO));
                    String nutrientiPiattoDB = cursor.getString(cursor.getColumnIndex(COLUMN_NUTRIENTI_PIATTO));
                    boolean personaliDB = cursor.getInt(cursor.getColumnIndex(COLUMN_PERSONALI)) == 1;
                    boolean favoritoDB = false;
                    int favIndex = cursor.getColumnIndex(COLUMN_FAVORITO);
                    if (favIndex >= 0) {
                        favoritoDB = cursor.getInt(favIndex) == 1;
                    }

                    Piatto nuovoPiatto = new Piatto(piattoIDDB, nomePiattoDB,portataPiattoDB, nutrientiPiattoDB,personaliDB, favoritoDB);
                    listaPiatti.add(nuovoPiatto);

                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) cursor.close();
            db.close();
        }

        return listaPiatti ;

    }
}
