package com.company.evernote_android.sync.rest;

import android.util.Log;

import com.company.evernote_android.sync.rest.callback.SendNoteCallback;
import com.company.evernote_android.utils.StatusCode;
import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.OnClientCallback;
import com.evernote.edam.type.Note;
import com.evernote.thrift.transport.TTransportException;

/**
 * Created by Zalman on 18.05.2015.
 */
public class SaveNoteRestMethod {
    private static String LOGTAG = "SaveNoteRestMethod: ";

    public SaveNoteRestMethod() {
    }

    public static void execute(final SendNoteCallback callback, final EvernoteSession mEvernoteSession, Note note, final long noteId) {

        if (mEvernoteSession.isLoggedIn()) {
            try {
                mEvernoteSession.getClientFactory().createNoteStoreClient().createNote(note, new OnClientCallback<Note>() {
                    @Override
                    public void onSuccess(Note data) {
                        callback.sendNote(data, StatusCode.OK, noteId);
                    }

                    @Override
                    public void onException(Exception exception) {
                        callback.sendNote(null, StatusCode.ERROR, noteId);
                    }
                });
            } catch (TTransportException exception) {
                Log.e(LOGTAG, "saveNotsendNote(null, StatusCode.ERROR, noteId);e exception:", exception);
            }
        }
    }
}
