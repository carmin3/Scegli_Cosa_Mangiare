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


    public DataBaseHelper(@Nullable Context context) {
        super(context, "customer.DB", null, 1);
    }

    //onCreate viene eseguito solo ed esclusivamente quando viene generato la prima volta il database.
    //bisogna quindi inserirci semplicemente le informazioni per crearlo
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableStatement = "CREATE TABLE " + PIATTO_TABLE + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NOME_PIATTO + " TEXT, " + COLUMN_PORTATA_PIATTO + " TEXT, "
                + COLUMN_NUTRIENTI_PIATTO + " TEXT, " + COLUMN_PERSONALI + " INTEGER DEFAULT 0)";

        db.execSQL(createTableStatement);


    }

     // questo comando si usa quando vogliamo fare dei cambiamenti sostanziali al database, evitando che si sfanculi tutto
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean addOne(Piatto piatto){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_NOME_PIATTO, piatto.getNomePiatto());
        cv.put(COLUMN_PORTATA_PIATTO, piatto.getPortata());
        cv.put(COLUMN_NUTRIENTI_PIATTO, piatto.getNutrienti());
        // store boolean as integer 1/0
        cv.put(COLUMN_PERSONALI, piatto.getPersonale() != null && piatto.getPersonale() ? 1 : 0);

        long insert = db.insert(PIATTO_TABLE, null, cv);
        db.close();
        return insert != -1;
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
                    int piattoIDDB = cursor.getInt(0);
                    String nomePiattoDB = cursor.getString(1);
                    String portataPiattoDB = cursor.getString(2);
                    String nutrientiPiattoDB = cursor.getString(3);
                    boolean personaliDB = cursor.getInt(4) == 1;

                    Piatto nuovoPiatto = new Piatto(piattoIDDB, nomePiattoDB,portataPiattoDB, nutrientiPiattoDB,personaliDB);
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
