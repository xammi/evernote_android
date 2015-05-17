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

/**
 * Created by Zalman on 12.04.2015.
 */
public class EvernoteService extends IntentService {

    public static final String INTENT_IDENTIFIER = "INTENT_IDENTIFIER";
    public static final String REQUEST_CALLBACK = "SERVICE_CALLBACK";
    public static final String ACTION_TYPE = "ACTION_TYPE";
    public static final String TYPE_GET_NOTES = "GET_NOTES";
    public static final String TYPE_GET_NOTEBOOKS = "GET_NOTEBOOKS";

    private Intent requestIntent;
    private ResultReceiver requestCallback;

    public EvernoteService() {
        super("EvernoteService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        requestIntent = intent;

        String requestType = requestIntent.getStringExtra(ACTION_TYPE);
        requestCallback = requestIntent.getParcelableExtra(REQUEST_CALLBACK);


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
                String guid = requestIntent.getStringExtra("guid");
                int maxNotes = requestIntent.getIntExtra("maxNotes", 0);
                NoteProcessor noteProcessor = new NoteProcessor(getApplicationContext());
                noteProcessor.getNotes(makeNoteProcessorCallback(), mEvernoteSession, guid, maxNotes);
                break;
        }

    }

    private ProcessorCallback makeNoteProcessorCallback() {
        ProcessorCallback callback = new ProcessorCallback() {
            @Override
            public void send(int resultCode) {
                if (requestCallback != null) {
                    requestCallback.send(resultCode, getRequestIntentBundle());
                }
            }
        };
        return callback;
    }

    private Bundle getRequestIntentBundle() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(INTENT_IDENTIFIER, requestIntent);
        return bundle;
    }

}
