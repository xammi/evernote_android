package com.company.evernote_android.sync;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.provider.ContactsContract;

import com.company.evernote_android.sync.processor.NoteProcessor;
import com.company.evernote_android.utils.EvernoteSessionConstant;
import com.company.evernote_android.sync.processor.NotebookProcessor;
import com.company.evernote_android.sync.processor.ProcessorCallback;
import com.company.evernote_android.utils.ParcelableNote;
import com.evernote.client.android.EvernoteSession;
import com.evernote.edam.type.Note;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Zalman on 12.04.2015.
 */
public class EvernoteService extends IntentService {

    public static final String INTENT_IDENTIFIER = "INTENT_IDENTIFIER";
    public static final String REQUEST_CALLBACK = "SERVICE_CALLBACK";
    public static final String ACTION_TYPE = "ACTION_TYPE";
    public static final String TYPE_GET_NOTES = "GET_NOTES";
    public static final String TYPE_GET_NOTEBOOKS = "GET_NOTEBOOKS";
    public static final String TYPE_SAVE_NOTEBOOK = "SAVE_NOTEBOOK";
    public static final String TYPE_SAVE_NOTE = "SAVE_NOTE";
    public static final String TYPE_UPDATE_NOTE = "UPDATE_NOTE";

    private Map<String, Intent> requestIntent = new HashMap<>();
    private ResultReceiver requestCallback;

    public EvernoteService() {
        super("EvernoteService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        String requestType = intent.getStringExtra(ACTION_TYPE);
        requestCallback = intent.getParcelableExtra(REQUEST_CALLBACK);
        requestIntent.put(requestType, intent);

        EvernoteSession mEvernoteSession = EvernoteSessionConstant.getSession(this);
        NotebookProcessor notebookProcessor;
        NoteProcessor noteProcessor;
        ParcelableNote parcelableNote;

        switch (requestType) {

            case TYPE_GET_NOTEBOOKS:
                notebookProcessor = new NotebookProcessor(getApplicationContext(), makeProcessorCallback());
                notebookProcessor.getNotebooks(mEvernoteSession);
                break;
            case TYPE_GET_NOTES:
                int maxNotes = intent.getIntExtra("maxNotes", 0);
                noteProcessor = new NoteProcessor(getApplicationContext(), makeProcessorCallback());
                noteProcessor.getNotes(mEvernoteSession, maxNotes);
                break;
            case TYPE_SAVE_NOTEBOOK:
                String notebookName = intent.getStringExtra("notebookName");
                notebookProcessor = new NotebookProcessor(getApplicationContext(), makeProcessorCallback());
                notebookProcessor.saveNotebook(mEvernoteSession, notebookName);
                break;
            case TYPE_SAVE_NOTE:
                parcelableNote = (ParcelableNote)intent.getParcelableExtra("parcelableNote");
                noteProcessor = new NoteProcessor(getApplicationContext(), makeProcessorCallback());
                noteProcessor.saveNote(mEvernoteSession, parcelableNote.toNote());
                break;
            case TYPE_UPDATE_NOTE:
                parcelableNote = (ParcelableNote)intent.getParcelableExtra("parcelableNote");
                noteProcessor = new NoteProcessor(getApplicationContext(), makeProcessorCallback());
                noteProcessor.updateNote(mEvernoteSession, parcelableNote.toNote());
                break;
        }

    }

    private ProcessorCallback makeProcessorCallback() {
        ProcessorCallback callback = new ProcessorCallback() {
            @Override
            public void send(int resultCode, String requestType) {
                if (requestCallback != null) {
                    requestCallback.send(resultCode, getRequestIntentBundle(requestType));
                }
            }
        };
        return callback;
    }

    private Bundle getRequestIntentBundle(String requestType) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(INTENT_IDENTIFIER, requestIntent.get(requestType));
        return bundle;
    }

}
