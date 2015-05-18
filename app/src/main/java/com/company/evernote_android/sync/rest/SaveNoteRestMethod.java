package com.company.evernote_android.sync.rest;

import android.util.Log;

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

    public static void execute(final SaveNoteCallback callback, final EvernoteSession mEvernoteSession, String title, String content, String notebookGuid, long created) {

        Note note = new Note();
        note.setTitle(title);
        note.setContent(content);
        note.setNotebookGuid(notebookGuid);
        note.setCreated(created);

        if (mEvernoteSession.isLoggedIn()) {
            try {
                mEvernoteSession.getClientFactory().createNoteStoreClient().createNote(note, new OnClientCallback<Note>() {
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
                Log.e(LOGTAG, "saveNotebook exception:", exception);
            }
        }
    }
}
