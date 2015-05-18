package com.company.evernote_android.sync.processor;

import android.content.ContentValues;
import android.content.Context;

import static com.company.evernote_android.provider.EvernoteContract.*;

import com.company.evernote_android.provider.DBConverter;
import com.company.evernote_android.sync.EvernoteService;
import com.company.evernote_android.sync.rest.UpdateNoteRestMethod;
import com.company.evernote_android.sync.rest.callback.SendNotesCallback;
import com.company.evernote_android.sync.rest.GetNotesRestMethod;
import com.company.evernote_android.sync.rest.callback.SendNoteCallback;
import com.company.evernote_android.sync.rest.SaveNoteRestMethod;
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

    public void  saveNote(EvernoteSession session, Note note) {
        SaveNoteRestMethod.execute(makeSaveNoteCallback(), session, note);
    }

    public void  updateNote(EvernoteSession session, Note note) {
        UpdateNoteRestMethod.execute(makeSaveNoteCallback(), session, note);
    }

    private SendNotesCallback makeGetNotesCallback() {
        SendNotesCallback callback = new SendNotesCallback() {
            @Override
            public void sendNotes(ConcurrentLinkedQueue<Note> notes, int statusCode) {
                // TODO нужно сделать нормальну синхронизацию, как для блокнотов
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
            public void sendNote(Note note, int statusCode) {

                // TODO обновить, sync = false
                if (statusCode == StatusCode.OK) {
                    // save Note in ContentProvide

                }

                processorCallback.send(statusCode, EvernoteService.TYPE_SAVE_NOTE);

            }
        };
        return callback;
    }

    private SendNoteCallback makeUpdateNoteCallback() {
        SendNoteCallback callback = new SendNoteCallback() {
            @Override
            public void sendNote(Note note, int statusCode) {

                // TODO обновить, sync = false
                if (statusCode == StatusCode.OK) {
                    // update Note in ContentProvide

                }

                processorCallback.send(statusCode, EvernoteService.TYPE_UPDATE_NOTE);

            }
        };
        return callback;
    }

}
