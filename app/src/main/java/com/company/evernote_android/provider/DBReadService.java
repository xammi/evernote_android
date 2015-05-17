package com.company.evernote_android.provider;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.os.IBinder;

import com.evernote.edam.type.Note;

import static com.company.evernote_android.provider.EvernoteContract.*;

/**
 * Created by max on 17.05.15.
 */
public class DBReadService extends Service implements DBReadAPI {
    private final IBinder mBinder = new DBReadBinder();

    public DBReadService() {}

    public class DBReadBinder extends Binder {
        public DBReadAPI getClientApiService() {
            return DBReadService.this;
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
                null);
        return cursor;
    }

    @Override
    public Cursor getNotesFor(long notebookId) {
        return null;
    }

    @Override
    public Note getNote(long noteId) {
        return null;
    }
}