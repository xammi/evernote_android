package com.company.evernote_android.sync.processor;

import android.content.Context;

import com.company.evernote_android.sync.EvernoteService;
import com.company.evernote_android.sync.rest.GetNotebooksCallback;
import com.company.evernote_android.sync.rest.GetNotebooksRestMethod;
import com.company.evernote_android.sync.rest.GetNotesCallback;
import com.company.evernote_android.sync.rest.GetNotesRestMethod;
import com.company.evernote_android.utils.StatusCode;
import com.evernote.client.android.EvernoteSession;
import com.evernote.edam.type.Note;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Zalman on 12.04.2015.
 */
public class NoteProcessor {

    private Context context;
    private ProcessorCallback processorCallback;

    public NoteProcessor(Context context) {
        this.context = context;
    }

    public void  getNotes(ProcessorCallback callback, EvernoteSession session, int maxNotes) {

        processorCallback = callback;
        GetNotesRestMethod.execute(makeGetNotesCallback(), session, maxNotes);

    }

    private GetNotesCallback makeGetNotesCallback() {
        GetNotesCallback callback = new GetNotesCallback() {
            @Override
            public void sendNotes(ConcurrentLinkedQueue<Note> notebooks, int statusCode) {

                if (statusCode == StatusCode.OK) {

                    // update Notes in ContentProvider

                }

                processorCallback.send(statusCode, EvernoteService.TYPE_GET_NOTES);

            }
        };
        return callback;
    }

}
