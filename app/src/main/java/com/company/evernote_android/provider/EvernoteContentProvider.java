package com.company.evernote_android.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

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

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        final SQLiteDatabase dbConnection = dbhelper.getWritableDatabase();
        long id = dbConnection.insertOrThrow(getTableName(uri), null, values);
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

        long id = Long.parseLong(uri.getLastPathSegment());
        selection = Notebooks._ID + "=" + id;
        int updated = dbConnection.update(getTableName(uri), values, selection, null);
        Uri result = ContentUris.withAppendedId(getContentUri(uri), id);

        return updated;
    }
}