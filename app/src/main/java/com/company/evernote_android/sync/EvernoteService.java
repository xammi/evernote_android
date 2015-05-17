package com.company.evernote_android.sync;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;

import com.company.evernote_android.sync.processor.NoteProcessor;
import com.company.evernote_android.utils.EvernoteSessionConstant;
import com.company.evernote_android.sync.processor.NotebookProcessor;
import com.company.evernote_android.sync.processor.ProcessorCallback;
import com.evernote.client.android.EvernoteSession;

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


        EvernoteSession mEvernoteSession = EvernoteSession.getInstance(this,
                EvernoteSessionConstant.CONSUMER_KEY,
                EvernoteSessionConstant.CONSUMER_SECRET,
                EvernoteSessionConstant.EVERNOTE_SERVICE,
                EvernoteSessionConstant.SUPPORT_APP_LINKED_NOTEBOOKS
        );

        switch (requestType) {
            case TYPE_GET_NOTEBOOKS:
                NotebookProcessor notebookProcessor = new NotebookProcessor(getApplicationContext());
                notebookProcessor.getNotebooks(makeNoteProcessorCallback(), mEvernoteSession);
                break;
            case TYPE_GET_NOTES:
                int maxNotes = intent.getIntExtra("maxNotes", 0);
                NoteProcessor noteProcessor = new NoteProcessor(getApplicationContext());
                noteProcessor.getNotes(makeNoteProcessorCallback(), mEvernoteSession, maxNotes);
                break;
        }

    }

    private ProcessorCallback makeNoteProcessorCallback() {
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
