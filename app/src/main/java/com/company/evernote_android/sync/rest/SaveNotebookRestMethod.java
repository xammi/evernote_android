package com.company.evernote_android.sync.rest;

import android.util.Log;

import com.company.evernote_android.sync.rest.callback.SendNotebookCallback;
import com.company.evernote_android.utils.StatusCode;
import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.OnClientCallback;
import com.evernote.edam.type.Notebook;
import com.evernote.thrift.transport.TTransportException;

/**
 * Created by Zalman on 17.05.2015.
 */
public class SaveNotebookRestMethod {

    private static String LOGTAG = "SaveNotebookRestMethod: ";

    public SaveNotebookRestMethod() {
    }

    public static void execute(final SendNotebookCallback callback, final EvernoteSession mEvernoteSession, String notebookName, final long notebookId) {

        final Notebook notebook = new Notebook();
        notebook.setName(notebookName);

        if (mEvernoteSession.isLoggedIn()) {
            try {
                mEvernoteSession.getClientFactory().createNoteStoreClient().createNotebook(notebook, new OnClientCallback<Notebook>() {
                    @Override
                    public void onSuccess(Notebook data) {
                        callback.sendNotebook(notebook, StatusCode.OK, notebookId);
                    }

                    @Override
                    public void onException(Exception exception) {
                        callback.sendNotebook(null, StatusCode.ERROR, notebookId);
                    }
                });

            } catch (TTransportException exception) {
                Log.e(LOGTAG, "saveNotebook exception:", exception);
            }
        }
    }
}
