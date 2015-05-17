package com.company.evernote_android.provider;

import android.database.Cursor;

import com.evernote.edam.type.Note;
import com.evernote.edam.type.Notebook;

/**
 * Created by max on 09.05.15.
 */
public interface ClientAPI {
    Cursor getAllNotebooks();
    Cursor getNotesFor(long notebookId);

    Notebook getNotebook(long notebookId);
    Note getNote(long noteId);

    boolean insertNotebook(String name);
    boolean insertNote(String title, String content, long notebookId);
    boolean updateNotebook(long notebookId, String name);
    boolean updateNote(long noteId, String title, String content, long notebookId);
    boolean deleteNote(long noteId);
    boolean deleteNotebook(long notebookId);
}
