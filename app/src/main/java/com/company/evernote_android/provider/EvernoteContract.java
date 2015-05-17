package com.company.evernote_android.provider;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by max on 09.05.2015.
 */
public final class EvernoteContract {
    public static final String AUTHORITY = "com.company.evernote_android.provider";
    public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);

    public static abstract class General implements BaseColumns {
        public static final String GUID = "guid";
        public static final String CREATED = "created";
        public static final String UPDATED = "updated";
        public static final String USN = "usn";
        public static final String STATE_DELETED = "state_deleted";
        public static final String STATE_SYNC_REQUIRED = "state_sync_required";
    }

    public static final class Notebooks extends General
    {
        public static final String TABLE_NAME = "notebooks";
        public static final String NAME = "name";

        public static final String[] ALL_COLUMNS_PROJECTION = {_ID, NAME, GUID, CREATED, UPDATED,
                USN, STATE_DELETED, STATE_SYNC_REQUIRED};

        static final String SQL_CREATE = "CREATE TABLE " + TABLE_NAME + " ( " +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + "," +
                NAME + " TEXT UNIQUE NOT NULL" + "," +
                GUID + " TEXT UNIQUE" + "," +
                CREATED + " INTEGER NOT NULL" + "," +
                UPDATED + " INTEGER NOT NULL" + "," +
                USN + " TEXT UNIQUE" + "," +
                STATE_DELETED + " INTEGER NOT NULL" + "," +
                STATE_SYNC_REQUIRED + " INTEGER NOT NULL" + ");";

        static final String SQL_DROP = "DROP TABLE IF EXISTS " + TABLE_NAME;

        public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, TABLE_NAME);
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.evernote.notebooks";
    }

    public static final class Notes extends General
    {
        public static final String TABLE_NAME = "notes";
        public static final String TITLE = "title";
        public static final String CONTENT = "content";
        public static final String NOTEBOOKS_ID = "notebooks_id";
        public static final String NOTEBOOKS_GUID = "notebooks_guid";

        public static final String[] ALL_COLUMNS_PROJECTION = {_ID, TITLE, CONTENT, GUID, CREATED,
                UPDATED, USN, STATE_DELETED, STATE_SYNC_REQUIRED, NOTEBOOKS_ID, NOTEBOOKS_GUID};

        static final String SQL_CREATE = "CREATE TABLE " + TABLE_NAME + "(" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + "," +
                TITLE + " TEXT NOT NULL" + "," +
                NOTEBOOKS_GUID + " TEXT NOT NULL" + "," +
                CONTENT + " TEXT" + "," +
                GUID + " TEXT UNIQUE" + "," +
                CREATED + " INTEGER NOT NULL" + "," +
                UPDATED + " INTEGER NOT NULL" + "," +
                USN + " INTEGER UNIQUE" + "," +
                STATE_DELETED + " INTEGER NOT NULL" + "," +
                STATE_SYNC_REQUIRED + " INTEGER NOT NULL" + "," +
                NOTEBOOKS_ID + " INTEGER NOT NULL" +
                " REFERENCES " + Notebooks.TABLE_NAME + " (" + Notebooks._ID + ")" +
                " ON DELETE CASCADE" + ");";

        static final String SQL_DROP = "DROP TABLE IF EXISTS " + TABLE_NAME;

        public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, TABLE_NAME);
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.evernote.notes";
    }

    public static enum StateDeleted {
        FALSE,
        TRUE
    }

    public static enum StateSyncRequired {
        PENDING,
        IN_PROCESS,
        SYNCED
    }
}