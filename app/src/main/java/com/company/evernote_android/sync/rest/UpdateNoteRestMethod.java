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
public class UpdateNoteRestMethod {
    private static String LOGTAG = "UpdateNoteRestMethod: ";

    public UpdateNoteRestMethod() {
    }

    public static void execute(final SendNoteCallback callback, final EvernoteSession mEvernoteSession, Note note) {

        if (mEvernoteSession.isLoggedIn()) {
            try {
                mEvernoteSession.getClientFactory().createNoteStoreClient().updateNote(note, new OnClientCallback<Note>() {
                    @Override
                    public void onSuccess(Note data) {
                        callback.sendNote(data, StatusCode.OK);
                    }

                    @Override
                    public void onException(Exception exception) {
                        callback.sendNote(null, StatusCode.ERROR);
                    }
                });
            } catch (TTransportException exception) {
                Log.e(LOGTAG, "updateNote exception:", exception);
            }
        }
    }
}
