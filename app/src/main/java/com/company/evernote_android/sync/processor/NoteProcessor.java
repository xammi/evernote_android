package com.company.evernote_android.sync.processor;

import android.content.ContentValues;
import android.content.Context;

import static com.company.evernote_android.provider.EvernoteContract.*;

import com.company.evernote_android.provider.DBConverter;
import com.company.evernote_android.sync.EvernoteService;
import com.company.evernote_android.sync.rest.DeleteNoteRestMethod;
import com.company.evernote_android.sync.rest.UpdateNoteRestMethod;
import com.company.evernote_android.sync.rest.callback.SendDataDeleteNoteCallback;
import com.company.evernote_android.sync.rest.callback.SendNotesCallback;
import com.company.evernote_android.sync.rest.GetNotesRestMethod;
import com.company.evernote_android.sync.rest.callback.SendNoteCallback;
import com.company.evernote_android.sync.rest.SaveNoteRestMethod;
import com.company.evernote_android.utils.StatusCode;
import com.evernote.client.android.EvernoteSession;
import com.evernote.edam.type.Note;

import java.util.Date;
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

    public void  saveNote(EvernoteSession session, Note note, long noteId) {
        SaveNoteRestMethod.execute(makeSaveNoteCallback(), session, note, noteId);
    }

    public void  updateNote(EvernoteSession session, Note note) {
        UpdateNoteRestMethod.execute(makeUpdateNoteCallback(), session, note);
    }

    public void  deleteNote(EvernoteSession session, String guid) {
        DeleteNoteRestMethod.execute(makeDeleteNoteCallback(), session, guid);
    }

    private SendNotesCallback makeGetNotesCallback() {
        SendNotesCallback callback = new SendNotesCallback() {
            @Override
            public void sendNotes(ConcurrentLinkedQueue<Note> notes, int statusCode) {

                if (statusCode == StatusCode.OK) {
                    for (Note note : notes) {
                        ContentValues contentValues = DBConverter.noteToValues(note);
                        context.getContentResolver().insert(Notes.CONTENT_URI, contentValues);
                    }
                }
                processorCallback.send(statusCode, EvernoteService.TYPE_GET_NOTES);
            }
        };
        return callback;
    }

    private SendNoteCallback makeSaveNoteCallback() {
        SendNoteCallback callback = new SendNoteCallback() {
            @Override
            public void sendNote(Note note, int statusCode, long noteId) {

                if (statusCode == StatusCode.OK) {
                    ContentValues contentValues = DBConverter.prepareNewUpdate(StateSyncRequired.SYNCED);
                    String WHERE_ID = Notes._ID + "=" + noteId;
                    context.getContentResolver().update(Notes.CONTENT_URI, contentValues, WHERE_ID, null);
                }
                processorCallback.send(statusCode, EvernoteService.TYPE_SAVE_NOTE);
            }
        };
        return callback;
    }

    private SendNoteCallback makeUpdateNoteCallback() {
        SendNoteCallback callback = new SendNoteCallback() {
            @Override
            public void sendNote(Note note, int statusCode, long noteId) {

                if (statusCode == StatusCode.OK) {
                    ContentValues contentValues = DBConverter.prepareNewUpdate(StateSyncRequired.SYNCED);
                    contentValues.put(Notes.GUID, note.getGuid());
                    String WHERE_ID = Notes.GUID + "=" + note.getGuid();
                    context.getContentResolver().update(Notes.CONTENT_URI, contentValues, WHERE_ID, null);
                }
                processorCallback.send(statusCode, EvernoteService.TYPE_UPDATE_NOTE);
            }
        };
        return callback;
    }

    private SendDataDeleteNoteCallback makeDeleteNoteCallback() {
        SendDataDeleteNoteCallback callback = new SendDataDeleteNoteCallback() {
            @Override
            public void sendInteger(Integer data, String guid, int statusCode) {
                // data - The Update Sequence Number for this change within the account.

                if (statusCode == StatusCode.OK) {
                    ContentValues contentValues = DBConverter.prepareNewUpdate(StateSyncRequired.SYNCED);
                    String WHERE_ID = Notes.GUID + "=" + guid;
                    context.getContentResolver().update(Notes.CONTENT_URI, contentValues, WHERE_ID, null);

                }
                processorCallback.send(statusCode, EvernoteService.TYPE_DELETE_NOTE);
            }
        };
        return callback;
    }
}
