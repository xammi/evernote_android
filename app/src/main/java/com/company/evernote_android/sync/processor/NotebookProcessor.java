package com.company.evernote_android.sync.processor;

import android.content.Context;

import com.company.evernote_android.sync.EvernoteService;
import com.company.evernote_android.sync.rest.GetNotebooksCallback;
import com.company.evernote_android.sync.rest.GetNotebooksRestMethod;
import com.company.evernote_android.utils.StatusCode;
import com.evernote.client.android.EvernoteSession;
import com.evernote.edam.type.Notebook;

import java.util.List;

/**
 * Created by Zalman on 17.05.2015.
 */
public class NotebookProcessor {

    private Context context;
    private ProcessorCallback processorCallback;

    public NotebookProcessor(Context context) {
        this.context = context;
    }

    public void  getNotebooks(ProcessorCallback callback, EvernoteSession session) {

        processorCallback = callback;
        GetNotebooksRestMethod.execute(makeGetNotebooksCallback(), session);

    }

    private GetNotebooksCallback makeGetNotebooksCallback() {
        GetNotebooksCallback callback = new GetNotebooksCallback() {
            @Override
            public void sendNotebooks(List<Notebook> notebooks, int statusCode) {

                if (statusCode == StatusCode.OK) {

                    // update Notebooks in ContentProvider

                }

                processorCallback.send(statusCode, EvernoteService.TYPE_GET_NOTEBOOKS);

            }
        };
        return callback;
    }

}
