package com.company.evernote_android.sync.processor;

import android.content.ContentValues;
import android.content.Context;

import static com.company.evernote_android.provider.EvernoteContract.*;

import com.company.evernote_android.provider.DBConverter;
import com.company.evernote_android.sync.EvernoteService;
import com.company.evernote_android.sync.rest.GetNotebooksCallback;
import com.company.evernote_android.sync.rest.GetNotebooksRestMethod;
import com.company.evernote_android.sync.rest.SaveNotebookCallback;
import com.company.evernote_android.sync.rest.SaveNotebookRestMethod;
import com.company.evernote_android.utils.StatusCode;
import com.evernote.client.android.EvernoteSession;
import com.evernote.edam.type.Notebook;

import java.util.Date;
import java.util.List;

/**
 * Created by Zalman on 17.05.2015.
 */
public class NotebookProcessor {

    private ProcessorCallback processorCallback;
    private Context context;

    public NotebookProcessor(Context context, ProcessorCallback callback) {
        this.processorCallback = callback;
        this.context = context;
    }

    public void  getNotebooks(EvernoteSession session) {
        GetNotebooksRestMethod.execute(makeGetNotebooksCallback(), session);
    }

    public void saveNotebook(EvernoteSession session, String notebookName) {
        SaveNotebookRestMethod.execute(makeSaveNotebookCallback(), session, notebookName);

    }

    private GetNotebooksCallback makeGetNotebooksCallback() {
        GetNotebooksCallback callback = new GetNotebooksCallback() {
            @Override
            public void sendNotebooks(List<Notebook> notebooks, int statusCode) {

                if (statusCode == StatusCode.OK) {
                    for (Notebook notebook : notebooks) {
                        ContentValues contentValues = DBConverter.notebookToValues(notebook);
                        context.getContentResolver().insert(Notebooks.CONTENT_URI, contentValues);
                    }
                }
                processorCallback.send(statusCode, EvernoteService.TYPE_GET_NOTEBOOKS);
            }
        };
        return callback;
    }

    private SaveNotebookCallback makeSaveNotebookCallback() {
        SaveNotebookCallback callback = new SaveNotebookCallback() {
            @Override
            public void sendNotebook(Notebook notebook, int statusCode) {

                if (statusCode == StatusCode.OK) {
                    ContentValues contentValues = DBConverter.notebookToValues(notebook);
                    context.getContentResolver().insert(Notebooks.CONTENT_URI, contentValues);
                }
                processorCallback.send(statusCode, EvernoteService.TYPE_SAVE_NOTEBOOK);
            }
        };
        return callback;
    }

}
