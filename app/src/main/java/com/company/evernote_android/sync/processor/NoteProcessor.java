package com.company.evernote_android.sync.processor;

import android.content.ContentValues;
import android.content.Context;

import static com.company.evernote_android.provider.EvernoteContract.*;
import com.company.evernote_android.sync.EvernoteService;
import com.company.evernote_android.sync.rest.GetNotesCallback;
import com.company.evernote_android.sync.rest.GetNotesRestMethod;
import com.company.evernote_android.utils.StatusCode;
import com.evernote.client.android.EvernoteSession;
import com.evernote.edam.type.Note;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Zalman on 12.04.2015.
 */
public class NoteProcessor {

    private ProcessorCallback processorCallback;
    private Context context;

    public NoteProcessor(Context context, ProcessorCallback callback) {
        this.processorCallback = callback;
        this.context = context;
    }

    public void  getNotes(EvernoteSession session, int maxNotes) {
        GetNotesRestMethod.execute(makeGetNotesCallback(), session, maxNotes);

    }

    private GetNotesCallback makeGetNotesCallback() {
        GetNotesCallback callback = new GetNotesCallback() {
            @Override
            public void sendNotes(ConcurrentLinkedQueue<Note> notes, int statusCode) {

                if (statusCode == StatusCode.OK) {
                    // update Notes in ContentProvide
                    for (Note note : notes) {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(Notes.TITLE, note.getTitle());
                        contentValues.put(Notes.CONTENT, note.getContent());
                        contentValues.put(Notes.CREATED, note.getCreated());
                        contentValues.put(Notes.UPDATED, note.getUpdated());

                        contentValues.put(Notes.NOTEBOOKS_GUID, note.getNotebookGuid());
                        contentValues.put(Notebooks.GUID, note.getGuid());
                        contentValues.put(Notes.STATE_DELETED, StateDeleted.FALSE.ordinal());

                        contentValues.put(Notes.STATE_SYNC_REQUIRED, StateSyncRequired.SYNCED.ordinal());
                        context.getContentResolver().insert(Notes.CONTENT_URI, contentValues);
                    }
                }

                processorCallback.send(statusCode, EvernoteService.TYPE_GET_NOTES);

            }
        };
        return callback;
    }

}
