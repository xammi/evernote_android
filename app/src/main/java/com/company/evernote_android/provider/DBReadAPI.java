package com.company.evernote_android.provider;

import android.database.Cursor;
import com.evernote.edam.type.Note;

/**
 * Created by max on 17.05.15.
 */
public interface DBReadAPI {
    Cursor getAllNotebooks();
    Cursor getNotesFor(long notebookId);
    Note getNote(long noteId);
}
