package com.company.evernote_android.provider;

import android.database.Cursor;
import android.database.SQLException;

import com.evernote.edam.type.Note;
import com.evernote.edam.type.Notebook;

/**
 * Created by max on 09.05.15.
 */
public interface ClientAPI {
    Cursor getAllNotebooks();
    Cursor getNotesFor(long notebookId);

    Cursor getUnsyncedNotebooks();
    Cursor getUnsyncedNotes();

    Notebook getNotebook(long notebookId);
    Note getNote(long noteId);

    long insertNotebook(String name) throws SQLException;
    long insertNote(String title, String content, long notebookId) throws SQLException;

    boolean updateNotebook(long notebookId, String name);
    boolean updateNote(long noteId, String title, String content, long notebookId);

    boolean deleteNote(long noteId);
    boolean deleteNotebook(long notebookId);
}
