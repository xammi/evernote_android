package com.company.evernote_android.sync.processor;

import android.content.Context;

import com.company.evernote_android.sync.rest.GetNotebooksCallback;
import com.company.evernote_android.sync.rest.GetNotebooksRestMethod;
import com.company.evernote_android.sync.rest.GetNotesCallback;
import com.company.evernote_android.sync.rest.GetNotesRestMethod;
import com.company.evernote_android.utils.StatusCode;
import com.evernote.client.android.EvernoteSession;
import com.evernote.edam.type.Note;

import java.util.List;

/**
 * Created by Zalman on 12.04.2015.
 */
public class NoteProcessor {

    private Context context;
    private ProcessorCallback processorCallback;

    public NoteProcessor(Context context) {
        this.context = context;
    }

    public void  getNotes(ProcessorCallback callback, EvernoteSession session, String guid, int maxNotes) {

        processorCallback = callback;
        GetNotesRestMethod.execute(makeGetNotesCallback(), session, guid, maxNotes);

    }

    private GetNotesCallback makeGetNotesCallback() {
        GetNotesCallback callback = new GetNotesCallback() {
            @Override
            public void sendNotes(List<Note> notebooks, int statusCode) {

                if (statusCode == StatusCode.OK) {

                    // update Notebooks in ContentProvider

                }

                processorCallback.send(statusCode);

            }
        };
        return callback;
    }

}
