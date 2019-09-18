package com.example.ammaryasser.musicstream;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Music",
            TABLE_NAME = "Info",
            COL_1 = "ID",
            COL_2 = "NAME",
            COL_3 = "LINK",
            COL_4 = "OFFLINE";
    private final Context context;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(String.format("CREATE TABLE %s (" +
                        "%s INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "%s TEXT," +
                        "%s TEXT NOT NULL," +
                        "%s BOOLEAN NULL)",
                TABLE_NAME, COL_1, COL_2, COL_3, COL_4));
        Toast.makeText(context, DATABASE_NAME + " created successfully", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
        Toast.makeText(context, DATABASE_NAME + " DROPped successfully", Toast.LENGTH_SHORT).show();
    }

    public boolean insert(String NAME, String LINK, boolean OFFLINE) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_2, NAME);
        values.put(COL_3, LINK);
        values.put(COL_4, OFFLINE);

        return db.insert(TABLE_NAME, null, values) == -1;
    }

    public Cursor select_All() {
        return getWritableDatabase().rawQuery("SELECT NAME, LINK FROM " + TABLE_NAME, null);
    }

    public void showMessage(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }
}
