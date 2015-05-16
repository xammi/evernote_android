package com.company.evernote_android.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import com.company.evernote_android.R;
import com.evernote.client.android.AsyncLinkedNoteStoreClient;
import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.OnClientCallback;
import com.evernote.edam.type.LinkedNotebook;
import com.evernote.edam.type.Note;
import com.evernote.thrift.transport.TTransportException;

import java.util.List;


public class SessionHolder extends ActionBarActivity {

    private static final String LOGTAG = "SessionHolder";

    private static final String CONSUMER_KEY = "streambuf-8430";
    private static final String CONSUMER_SECRET = "e92a48e112672396";


    private static final EvernoteSession.EvernoteService EVERNOTE_SERVICE = EvernoteSession.EvernoteService.SANDBOX;
    private static final boolean SUPPORT_APP_LINKED_NOTEBOOKS = true;

    protected EvernoteSession mEvernoteSession;
    protected final int DIALOG_PROGRESS = 101;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEvernoteSession = EvernoteSession.getInstance(this,
                CONSUMER_KEY,
                CONSUMER_SECRET,
                EVERNOTE_SERVICE,
                SUPPORT_APP_LINKED_NOTEBOOKS
        );
    }

    // using createDialog, could use Fragments instead
    @SuppressWarnings("deprecation")
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_PROGRESS:
                return new ProgressDialog(SessionHolder.this);
        }
        return super.onCreateDialog(id);
    }

    @Override
    @SuppressWarnings("deprecation")
    protected void onPrepareDialog(int id, Dialog dialog) {
        switch (id) {
            case DIALOG_PROGRESS:
                ((ProgressDialog) dialog).setIndeterminate(true);
                dialog.setCancelable(false);
                ((ProgressDialog) dialog).setMessage(getString(R.string.esdk__loading));
        }
    }

    /**
     * Helper method for apps that have access to a single notebook, and that notebook is a linked
     * notebook ... find that notebook, gets access to it, and calls back to the caller.
     * @param callback invoked on error or with a client to the linked notebook
     */
    protected void invokeOnAppLinkedNotebook(final OnClientCallback<Pair<AsyncLinkedNoteStoreClient, LinkedNotebook>> callback) {
        try {
            // We need to get the one and only linked notebook
            mEvernoteSession.getClientFactory().createNoteStoreClient().listLinkedNotebooks(new OnClientCallback<List<LinkedNotebook>>() {
                @Override
                public void onSuccess(List<LinkedNotebook> linkedNotebooks) {
                    // We should only have one linked notebook
                    if (linkedNotebooks.size() != 1) {
                        Log.e(LOGTAG, "Error getting linked notebook - more than one linked notebook");
                        callback.onException(new Exception("Not single linked notebook"));
                    } else {
                        final LinkedNotebook linkedNotebook = linkedNotebooks.get(0);
                        mEvernoteSession.getClientFactory().createLinkedNoteStoreClientAsync(linkedNotebook, new OnClientCallback<AsyncLinkedNoteStoreClient>() {
                            @Override
                            public void onSuccess(AsyncLinkedNoteStoreClient asyncLinkedNoteStoreClient) {
                                // Finally create the note in the linked notebook
                                callback.onSuccess(new Pair<AsyncLinkedNoteStoreClient, LinkedNotebook>(asyncLinkedNoteStoreClient, linkedNotebook));
                            }

                            @Override
                            public void onException(Exception exception) {
                                callback.onException(exception);
                            }
                        });
                    }
                }

                @Override
                public void onException(Exception exception) {
                    callback.onException(exception);
                }
            });
        } catch (TTransportException exception) {
            callback.onException(exception);
        }
    }

    /**
     * Creates the specified note in an app's linked notebook.  Used when an app only has access to
     * a single notebook, and that notebook is a linked notebook.
     * @param note the note to be created
     * @param createNoteCallback called on success or failure
     */
    protected void createNoteInAppLinkedNotebook(final Note note, final OnClientCallback<Note> createNoteCallback) {
        showDialog(DIALOG_PROGRESS);
        invokeOnAppLinkedNotebook(new OnClientCallback<Pair<AsyncLinkedNoteStoreClient, LinkedNotebook>>() {
            @Override
            public void onSuccess(final Pair<AsyncLinkedNoteStoreClient, LinkedNotebook> pair) {
                // Rely on the callback to dismiss the dialog
                pair.first.createNoteAsync(note, pair.second, createNoteCallback);
            }

            @Override
            public void onException(Exception exception) {
                Log.e(LOGTAG, "Error creating linked notestore", exception);
                Toast.makeText(getApplicationContext(), R.string.error_creating_notestore, Toast.LENGTH_LONG).show();
                removeDialog(DIALOG_PROGRESS);
            }
        });
    }
}
