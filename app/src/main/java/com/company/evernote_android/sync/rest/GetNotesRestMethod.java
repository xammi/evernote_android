package com.company.evernote_android.sync.rest;

import com.company.evernote_android.utils.StatusCode;
import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.OnClientCallback;
import com.evernote.edam.notestore.NoteFilter;
import com.evernote.edam.notestore.NotesMetadataList;
import com.evernote.edam.notestore.NotesMetadataResultSpec;
import com.evernote.edam.type.Note;
import com.evernote.thrift.transport.TTransportException;

import java.net.URI;
import java.util.List;

/**
 * Created by Zalman on 12.04.2015.
 */
public class GetNotesRestMethod {

    public GetNotesRestMethod() {
    }

    public static void execute(final GetNotesCallback callback, final EvernoteSession mEvernoteSession, String guid, int maxNotes) {

        if (mEvernoteSession.isLoggedIn()) {
            NoteFilter filter = new NoteFilter();
            filter.setNotebookGuid(guid);

            NotesMetadataResultSpec spec = new NotesMetadataResultSpec();
            spec.setIncludeTitle(true);
            try {
                mEvernoteSession.getClientFactory().createNoteStoreClient().findNotesMetadata(filter, 0, maxNotes, spec, new OnClientCallback<NotesMetadataList>() {
                    @Override
                    public void onSuccess(NotesMetadataList data) {
                        int z = 6;
                    }

                    @Override
                    public void onException(Exception exception) {
                        int a = 5;
                    }
                });
            } catch (TTransportException exception) {
                int b = 6;
            }
        }
    }

}
