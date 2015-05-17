package com.company.evernote_android.sync.rest;

import android.util.Log;

import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.OnClientCallback;
import com.evernote.edam.notestore.NoteFilter;
import com.evernote.edam.notestore.NotesMetadataList;
import com.evernote.edam.notestore.NotesMetadataResultSpec;
import com.evernote.edam.type.Notebook;
import com.evernote.thrift.transport.TTransportException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zalman on 16.05.2015.
 */
public class GetNotebooksRestMethod {

    private static String LOGTAG = "GetNotebooksRestMethod";

    public GetNotebooksRestMethod() {
    }

    public static void execute(final EvernoteSession mEvernoteSession) {

            if (mEvernoteSession.isLoggedIn()) {
                try {
                    mEvernoteSession.getClientFactory().createNoteStoreClient().listNotebooks(new OnClientCallback<List<Notebook>>() {
                        @Override
                        public void onSuccess(final List<Notebook> notebooks) {
                            List<String> namesList = new ArrayList<String>(notebooks.size());


                            NoteFilter filter = new NoteFilter();
                            filter.setNotebookGuid(notebooks.get(1).getGuid());

                            NotesMetadataResultSpec spec = new NotesMetadataResultSpec();
                            spec.setIncludeTitle(true);
                            try {
                                mEvernoteSession.getClientFactory().createNoteStoreClient().findNotesMetadata(filter, 0, 10, spec, new OnClientCallback<NotesMetadataList>() {
                                    @Override
                                    public void onSuccess(NotesMetadataList data) {
                                        NotesMetadataList b = data;
                                    }

                                    @Override
                                    public void onException(Exception exception) {
                                        Log.e(LOGTAG, "Error retrieving notes", exception);
                                    }
                                });
                            } catch (TTransportException exception) {
                                Log.e(LOGTAG, "Error retrieving notes", exception);
                            }


                            for (Notebook notebook : notebooks) {
                                namesList.add(notebook.getName());
                            }

                            //String notebookNames = TextUtils.join(", ", namesList);
                            //Toast.makeText(getApplicationContext(), notebookNames + " notebooks have been retrieved", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onException(Exception exception) {
                            Log.e(LOGTAG, "Error retrieving notebooks", exception);
                        }
                    });

                } catch (TTransportException e) {
                    e.getMessage();
                }
            }
        }

}
