package com.company.evernote_android.provider;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Zalman on 12.04.2015.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "evernote.db";
    private static final int DATABASE_VERSION = 2;

    public DatabaseHelper(Context context, CursorFactory factory) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    public DatabaseHelper(Context context, CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, version);
    }

    public DatabaseHelper(Context context, CursorFactory factory, DatabaseErrorHandler errorHandler) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(EvernoteContract.Notebooks.SQL_CREATE);
        db.execSQL(EvernoteContract.Notes.SQL_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(EvernoteContract.Notebooks.SQL_DROP);
        db.execSQL(EvernoteContract.Notes.SQL_DROP);
        this.onCreate(db);
    }
}
