package com.company.evernote_android.sync.rest;

import android.util.Log;

import com.company.evernote_android.utils.StatusCode;
import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.OnClientCallback;
import com.evernote.edam.notestore.NoteFilter;
import com.evernote.edam.notestore.NoteMetadata;
import com.evernote.edam.notestore.NotesMetadataList;
import com.evernote.edam.notestore.NotesMetadataResultSpec;
import com.evernote.edam.type.Note;
import com.evernote.edam.type.Notebook;
import com.evernote.thrift.transport.TTransportException;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Zalman on 12.04.2015.
 */
public class GetNotesRestMethod {

    private static String LOGTAG = "GetNotesRestMethod: ";
    private static AtomicInteger counterEnd = new AtomicInteger(0);
    private static AtomicInteger counterNotes = new AtomicInteger(0);

    public GetNotesRestMethod() {
    }

    public static void execute(final GetNotesCallback callback, final EvernoteSession mEvernoteSession, final String guid, final int maxNotes) {

        if (mEvernoteSession.isLoggedIn()) {

            try {
                mEvernoteSession.getClientFactory().createNoteStoreClient().listNotebooks(new OnClientCallback<List<Notebook>>() {
                    @Override
                    public void onSuccess(final List<Notebook> notebooks) {


                        for (Notebook notebook : notebooks) {
                            NoteFilter filter = new NoteFilter();
                            filter.setNotebookGuid(notebook.getGuid());

                            NotesMetadataResultSpec spec = new NotesMetadataResultSpec();
                            spec.setIncludeTitle(true);
                            try {
                                mEvernoteSession.getClientFactory().createNoteStoreClient().findNotesMetadata(filter, 0, maxNotes, spec, new OnClientCallback<NotesMetadataList>() {
                                    @Override
                                    public void onSuccess(NotesMetadataList data) {

                                        List<NoteMetadata> mNotes = data.getNotes();

                                        counterNotes.addAndGet(mNotes.size());
                                        for (final NoteMetadata mNote : mNotes) {
                                            try {
                                                mEvernoteSession.getClientFactory().createNoteStoreClient().getNote(mNote.getGuid(), true, true, true, true, new OnClientCallback<Note>() {
                                                    @Override
                                                    public void onSuccess(Note data) {
                                                        counterEnd.getAndAdd(1);
                                                        Log.e(mNote.getGuid(), data.getTitle());
                                                        if (counterNotes.get() == counterEnd.get()) {
                                                            Log.e("send", "ok");
                                                        }

                                                    }
                                                    @Override
                                                    public void onException(Exception exception) {
                                                        counterEnd.getAndAdd(1);
                                                        Log.e(LOGTAG, "getNote exception:", exception);
                                                        if (counterNotes.get() == counterEnd.get()) {
                                                            Log.e("send", "error");
                                                        }

                                                    }
                                                });
                                            } catch (TTransportException exception) {
                                                Log.e(LOGTAG, "getNote exception:", exception);
                                            }
                                        }
                                    }
                                    @Override
                                    public void onException(Exception exception) {
                                        Log.e(LOGTAG, "getNotebook exception:", exception);
                                    }
                                });
                            } catch (TTransportException exception) {
                                Log.e(LOGTAG, "getNotebook exception:", exception);
                            }
                        }
                    }
                    @Override
                    public void onException(Exception exception) {
                        Log.e(LOGTAG, "getNotesMetadataList exception:", exception);
                    }
                });
            } catch (TTransportException exception) {
                Log.e(LOGTAG, "getNotesMetadataList exception:", exception);
            }
        }
        Log.e("test", "ok");
    }

}
