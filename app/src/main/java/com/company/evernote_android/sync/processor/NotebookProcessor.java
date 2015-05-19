package com.company.evernote_android.sync.processor;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import static com.company.evernote_android.provider.EvernoteContract.*;

import com.company.evernote_android.provider.DBConverter;
import com.company.evernote_android.sync.EvernoteService;
import com.company.evernote_android.sync.rest.callback.SendNotebooksCallback;
import com.company.evernote_android.sync.rest.GetNotebooksRestMethod;
import com.company.evernote_android.sync.rest.callback.SendNotebookCallback;
import com.company.evernote_android.sync.rest.SaveNotebookRestMethod;
import com.company.evernote_android.utils.StatusCode;
import com.evernote.client.android.EvernoteSession;
import com.evernote.edam.type.Notebook;

import android.database.SQLException;
import java.util.List;

/**
 * Created by Zalman on 17.05.2015.
 */
public class NotebookProcessor {

    private static final String LOGTAG = "NotebookProcessor";

    private ProcessorCallback processorCallback;
    private Context context;

    public NotebookProcessor(Context context, ProcessorCallback callback) {
        this.processorCallback = callback;
        this.context = context;
    }

    public void  getNotebooks(EvernoteSession session) {
        GetNotebooksRestMethod.execute(makeGetNotebooksCallback(), session);
    }

    public void saveNotebook(EvernoteSession session, String notebookName, long notebookId) {
        SaveNotebookRestMethod.execute(makeSaveNotebookCallback(), session, notebookName, notebookId);

    }

    private SendNotebooksCallback makeGetNotebooksCallback() {
        SendNotebooksCallback callback = new SendNotebooksCallback() {
            @Override
            public void sendNotebooks(List<Notebook> notebooks, int statusCode) {

                if (statusCode == StatusCode.OK) {
                    for (Notebook notebook : notebooks) {
                        ContentValues contentValues = DBConverter.notebookToValues(notebook);
                        try {
                            context.getContentResolver().insert(Notebooks.CONTENT_URI, contentValues);
                        }
                        catch (SQLException e) {
                            Log.e(LOGTAG, e.getMessage());
                        }
                    }
                }
                processorCallback.send(statusCode, EvernoteService.TYPE_GET_NOTEBOOKS);
            }
        };
        return callback;
    }

    private SendNotebookCallback makeSaveNotebookCallback() {
        SendNotebookCallback callback = new SendNotebookCallback() {
            @Override
            public void sendNotebook(Notebook notebook, int statusCode, long notebookId) {

                if (statusCode == StatusCode.OK) {
                    ContentValues contentValues = DBConverter.prepareNewUpdate(StateSyncRequired.SYNCED);
                    contentValues.put(Notebooks.GUID, notebook.getGuid());
                    String WHERE_ID = Notebooks._ID + "=" + notebookId;
                    context.getContentResolver().update(Notebooks.CONTENT_URI, contentValues, WHERE_ID, null);
                }
                processorCallback.send(statusCode, EvernoteService.TYPE_SAVE_NOTEBOOK);
            }
        };
        return callback;
    }

}
