package com.company.evernote_android.provider;

import android.content.ContentValues;

import com.evernote.edam.type.Note;
import com.evernote.edam.type.Notebook;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.company.evernote_android.provider.EvernoteContract.*;

/**
 * Created by max on 18.05.15.
 */
public class DBConverter {

    public static ContentValues notebookToValues(Notebook notebook) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Notebooks.NAME, notebook.getName());
        contentValues.put(Notebooks.CREATED, notebook.getServiceCreated());
        contentValues.put(Notebooks.UPDATED, notebook.getServiceUpdated());

        contentValues.put(Notebooks.GUID, notebook.getGuid());

        contentValues.put(Notebooks.STATE_DELETED, EvernoteContract.StateDeleted.FALSE.ordinal());
        contentValues.put(Notebooks.STATE_SYNC_REQUIRED, EvernoteContract.StateSyncRequired.SYNCED.ordinal());
        return contentValues;
    }

    public static ContentValues noteToValues(Note note) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Notes.TITLE, note.getTitle());

        final Pattern pattern = Pattern.compile("<en-note>(.+?)</en-note>");
        final Matcher matcher = pattern.matcher(note.getContent());
        matcher.find();

        contentValues.put(Notes.CONTENT, matcher.group(1));
        contentValues.put(Notes.CREATED, note.getCreated());
        contentValues.put(Notes.UPDATED, note.getUpdated());

        contentValues.put(Notes.NOTEBOOKS_GUID, note.getNotebookGuid());
        contentValues.put(Notebooks.GUID, note.getGuid());

        if (note.getDeleted() == 0) {
            contentValues.put(Notes.STATE_DELETED, StateDeleted.FALSE.ordinal());
        }
        else {
            contentValues.put(Notes.STATE_DELETED, StateDeleted.TRUE.ordinal());
        }

        contentValues.put(Notes.STATE_SYNC_REQUIRED, StateSyncRequired.SYNCED.ordinal());
        return contentValues;
    }

    public static ContentValues prepareNewUpdate() {
        ContentValues contentValues = new ContentValues();
        Long currentTime = new Date().getTime();
        contentValues.put(General.UPDATED, currentTime);
        contentValues.put(General.STATE_SYNC_REQUIRED, StateSyncRequired.SYNCED.ordinal());
        return contentValues;
    }
}
