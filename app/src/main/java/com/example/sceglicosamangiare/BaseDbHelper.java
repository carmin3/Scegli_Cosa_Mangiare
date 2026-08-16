package com.example.sceglicosamangiare;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
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
                InputStream is = ctx.getAssets().open(assetName);
                FileOutputStream fos = new FileOutputStream(destFile);
                byte[] buf = new byte[4096];
                int r;
                while ((r = is.read(buf)) > 0) fos.write(buf, 0, r);
                fos.close();
                is.close();
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
