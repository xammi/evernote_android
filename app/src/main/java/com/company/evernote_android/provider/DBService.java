package com.company.evernote_android.provider;

/**
 * Created by max on 09.05.15.
 */

import android.app.Service;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;

import com.evernote.edam.type.Note;
import com.evernote.edam.type.Notebook;

import java.util.Date;

import static com.company.evernote_android.provider.EvernoteContract.*;

public class DBService extends Service implements ClientAPI {
    private final IBinder mBinder = new DBWriteBinder();

    public DBService() {}

    public class DBWriteBinder extends Binder {
        public ClientAPI getClientApiService() {
            return DBService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public Cursor getAllNotebooks() {
        String NOT_DELETED_SELECTION = Notebooks.STATE_DELETED + "=" + StateDeleted.FALSE.ordinal();
        Cursor cursor = getContentResolver().query(
                Notebooks.CONTENT_URI,
                Notebooks.ALL_COLUMNS_PROJECTION,
                NOT_DELETED_SELECTION,
                null,
                Notes.UPDATED + " DESC");
        return cursor;
    }

    @Override
    public Cursor getNotesFor(long notebookId) {
        String selection = Notes.STATE_DELETED + "=" + StateDeleted.FALSE.ordinal()
                + " AND " + Notes.NOTEBOOKS_ID + "=" + ((Long) notebookId).toString();
        Cursor cursor = getContentResolver().query(
                Notebooks.CONTENT_URI,
                Notebooks.ALL_COLUMNS_PROJECTION,
                selection,
                null,
                Notes.UPDATED + " DESC");
        return cursor;
    }

    public Notebook getNotebook(long notebookId) {
        String WHERE_ID = Notebooks._ID + "=" + ((Long) notebookId).toString();
        Cursor cursor = getContentResolver().query(
                Notebooks.CONTENT_URI,
                Notebooks.ALL_COLUMNS_PROJECTION,
                WHERE_ID,
                null,
                null
        );
        if (cursor == null || cursor.getCount() == 0)
            return null;
        else {
            Notebook notebook = new Notebook();
            cursor.moveToNext();
            notebook.setName(cursor.getString(cursor.getColumnIndexOrThrow(Notebooks.NAME)));
            notebook.setGuid(cursor.getString(cursor.getColumnIndexOrThrow(Notebooks.GUID)));
            cursor.close();
            return notebook;
        }
    }

    @Override
    public Note getNote(long noteId) {
        String WHERE_ID = Notes._ID + "=" + ((Long) noteId).toString();
        Cursor cursor = getContentResolver().query(
                Notes.CONTENT_URI,
                Notes.ALL_COLUMNS_PROJECTION,
                WHERE_ID,
                null,
                null
        );
        if (cursor == null || cursor.getCount() == 0)
            return null;
        else {
            Note note = new Note();
            cursor.moveToNext();
            note.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(Notes.TITLE)));
            note.setContent(cursor.getString(cursor.getColumnIndexOrThrow(Notes.CONTENT)));
            note.setCreated(cursor.getLong(cursor.getColumnIndexOrThrow(Notes.CREATED)));
            note.setUpdated(cursor.getLong(cursor.getColumnIndexOrThrow(Notes.UPDATED)));
            note.setNotebookGuid(cursor.getString(cursor.getColumnIndexOrThrow(Notes.NOTEBOOKS_GUID)));
            note.setDeleted(cursor.getLong(cursor.getColumnIndexOrThrow(Notes.NOTEBOOKS_ID))); // feature
            cursor.close();
            return note;
        }
    }

    @Override
    public long insertNotebook(String name) {
        ContentValues contentValues = new ContentValues();
        Long currentTime = new Date().getTime();
        contentValues.put(Notebooks.NAME, name);
        contentValues.put(Notebooks.CREATED, currentTime);
        contentValues.put(Notebooks.UPDATED, currentTime);
        contentValues.put(Notebooks.STATE_DELETED, StateDeleted.FALSE.ordinal());
        contentValues.put(Notebooks.STATE_SYNC_REQUIRED, StateSyncRequired.PENDING.ordinal());
        Uri result = getContentResolver().insert(Notebooks.CONTENT_URI, contentValues);
        return Long.parseLong(result.getLastPathSegment());
    }

    @Override
    public long insertNote(String title, String content, long notebookId) {
        ContentValues contentValues = new ContentValues();
        Long currentTime = new Date().getTime();
        contentValues.put(Notes.TITLE, title);
        contentValues.put(Notes.CONTENT, content);
        contentValues.put(Notes.CREATED, currentTime);
        contentValues.put(Notes.UPDATED, currentTime);
        contentValues.put(Notes.NOTEBOOKS_ID, notebookId);
        contentValues.put(Notes.STATE_DELETED, StateDeleted.FALSE.ordinal());
        contentValues.put(Notes.STATE_SYNC_REQUIRED, StateSyncRequired.PENDING.ordinal());
        Uri result = getContentResolver().insert(Notes.CONTENT_URI, contentValues);
        return Long.parseLong(result.getLastPathSegment());
    }

    @Override
    public boolean updateNotebook(long notebookId, String name) {
        ContentValues contentValues = new ContentValues();
        Long currentTime = new Date().getTime();
        contentValues.put(Notebooks.NAME, name);
        contentValues.put(Notebooks.UPDATED, currentTime);
        contentValues.put(Notebooks.STATE_SYNC_REQUIRED, StateSyncRequired.PENDING.ordinal());

        String WHERE_ID = Notebooks._ID + "=" + ((Long) notebookId).toString();
        int result = getContentResolver().update(Notebooks.CONTENT_URI, contentValues, WHERE_ID, null);
        return result != 0;
    }

    @Override
    public boolean updateNote(long noteId, String title, String content, long notebookId) {
        ContentValues contentValues = new ContentValues();
        Long currentTime = new Date().getTime();
        contentValues.put(Notes.TITLE, title);
        contentValues.put(Notes.CONTENT, content);
        contentValues.put(Notes.UPDATED, currentTime);
        contentValues.put(Notes.NOTEBOOKS_ID, notebookId);
        contentValues.put(Notebooks.STATE_SYNC_REQUIRED, StateSyncRequired.PENDING.ordinal());

        String WHERE_ID = Notes._ID + "=" + ((Long) noteId).toString();
        int result = getContentResolver().update(Notes.CONTENT_URI, contentValues, WHERE_ID, null);
        return result != 0;
    }

    @Override
    public boolean deleteNotebook(long notebookId) {
        String WHERE_ID = Notebooks._ID + "=" + ((Long) notebookId).toString();
        int result = getContentResolver().delete(Notebooks.CONTENT_URI, WHERE_ID, null);
        return result != 0;
    }

    @Override
    public boolean deleteNote(long noteId) {
        String WHERE_ID = Notebooks._ID + "=" + ((Long) noteId).toString();
        int result = getContentResolver().delete(Notes.CONTENT_URI, WHERE_ID, null);
        return result != 0;
    }
}
