package com.company.evernote_android.sync.processor;

import android.content.Context;

import com.company.evernote_android.sync.rest.GetNotebooksRestMethod;
import com.evernote.client.android.EvernoteSession;

/**
 * Created by Zalman on 17.05.2015.
 */
public class NotebookProcessor {

    private Context context;

    public NotebookProcessor(Context context) {
        this.context = context;
    }

    public void  getNotebooks(ProcessorCallback callback, EvernoteSession session) {

        GetNotebooksRestMethod.execute(session);

        // update NoteContentProvider

        //callback.send();

    }

}
