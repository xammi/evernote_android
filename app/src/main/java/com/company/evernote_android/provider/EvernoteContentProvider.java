package com.company.evernote_android.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.ContactsContract;

import com.evernote.edam.type.Notebook;

import static com.company.evernote_android.provider.EvernoteContract.*;

/**
 * Created by max on 09.05.15.
 */
public class EvernoteContentProvider extends ContentProvider {

    private DatabaseHelper dbhelper;

    private static final UriMatcher URI_MATCHER;
    private static final int NOTEBOOKS = 0;
    private static final int NOTES = 1;

    static {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI(AUTHORITY, Notebooks.TABLE_NAME, NOTEBOOKS);
        URI_MATCHER.addURI(AUTHORITY, Notes.TABLE_NAME, NOTES);
    }

    @Override
    public String getType(Uri uri) {
        switch (URI_MATCHER.match(uri)) {
            case NOTEBOOKS:
                return Notebooks.CONTENT_TYPE;
            case NOTES:
                return Notes.CONTENT_TYPE;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    public String getTableName(Uri uri) {
        switch (URI_MATCHER.match(uri)) {
            case NOTEBOOKS:
                return Notebooks.TABLE_NAME;
            case NOTES:
                return Notes.TABLE_NAME;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    public Uri getContentUri(Uri uri) {
        switch (URI_MATCHER.match(uri)) {
            case NOTEBOOKS:
                return Notebooks.CONTENT_URI;
            case NOTES:
                return Notes.CONTENT_URI;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public boolean onCreate() {
        dbhelper = new DatabaseHelper(getContext(), null);
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder)
    {
        SQLiteDatabase database = dbhelper.getReadableDatabase();
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(getTableName(uri));

        Cursor cursor = builder.query(database, projection, selection, selectionArgs, null, null, sortOrder);
        return cursor;
    }

    private Long getNotebookByGUID(String GUID) {
        String WHERE_ID = Notebooks.GUID + "='" + GUID + "'";
        Cursor cursor = query(Notebooks.CONTENT_URI, new String[]{Notebooks._ID}, WHERE_ID, null, null);
        if (cursor == null || cursor.getCount() == 0) {
            return null;
        }
        else {
            cursor.moveToNext();
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(Notebooks._ID));
            cursor.close();
            return id;
        }
    }

    private Long getNotebookByID(String ID) {
        String WHERE_ID = Notebooks._ID + "='" + ID + "'";
        Cursor cursor = query(Notebooks.CONTENT_URI, new String[]{Notebooks._ID}, WHERE_ID, null, null);
        if (cursor == null || cursor.getCount() == 0) {
            return null;
        }
        else {
            cursor.moveToNext();
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(Notebooks._ID));
            cursor.close();
            return id;
        }
    }

    private Long getIdByWhere(Uri uri, String WHERE) {
        Cursor cursor = query(getContentUri(uri), new String[]{General._ID}, WHERE, null, null);
        cursor.moveToNext();
        long id = cursor.getLong(cursor.getColumnIndexOrThrow(General._ID));
        cursor.close();
        return id;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) throws SQLException {
        String tableName = getTableName(uri);

        if (values.containsKey(Notes.NOTEBOOKS_GUID) && !values.containsKey(Notes.NOTEBOOKS_ID)) {
            Long notebookId = getNotebookByGUID(values.getAsString(Notes.NOTEBOOKS_GUID));
            values.put(Notes.NOTEBOOKS_ID, notebookId);
        }

        if (!values.containsKey(Notes.NOTEBOOKS_GUID) && values.containsKey(Notes.NOTEBOOKS_ID)) {
            Long notebookId = getNotebookByID(values.getAsString(Notes.NOTEBOOKS_ID));
            values.put(Notes.NOTEBOOKS_GUID, notebookId);
        }

        final SQLiteDatabase dbConnection = dbhelper.getWritableDatabase();
        long id = 0;
        try {
            id = dbConnection.insertOrThrow(tableName, null, values);
        }
        catch (SQLException e) {
            String WHERE_ID = General.GUID + "='" + values.getAsString(General.GUID) + "'";
            int updated = dbConnection.update(tableName, values, WHERE_ID, null);

            if (updated == 0) {
                throw new SQLException("Такой элемент уже существует");
            }
            id = getIdByWhere(uri, WHERE_ID);
        }
        Uri result = ContentUris.withAppendedId(getContentUri(uri), id);
        return result;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase dbConnection = dbhelper.getWritableDatabase();
        int deleted = dbConnection.delete(getTableName(uri), selection, selectionArgs);
        return deleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs)
    {
        final SQLiteDatabase dbConnection = dbhelper.getWritableDatabase();
        int updated = dbConnection.update(getTableName(uri), values, selection, null);
        return updated;
    }
}