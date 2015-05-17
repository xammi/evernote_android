package com.company.evernote_android.provider;

import android.database.Cursor;

import com.evernote.edam.type.Note;

/**
 * Created by max on 09.05.15.
 */
public interface ClientAPI {
    Cursor getAllNotebooks();
    Cursor getNotesFor(long notebookId);
    Note getNote(long noteId);

    boolean insertNotebook(String name);
    boolean insertNote(String title, String content, long notebooksId);
    boolean updateNotebook(long notebooksId, String name);
    boolean updateNote(String title, String content, long notesId);
    boolean deleteNote(long notesId);
    boolean deleteNotebook(long notebooksId);
}
