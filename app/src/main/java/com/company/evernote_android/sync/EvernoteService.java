package com.company.evernote_android.sync;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;

import com.company.evernote_android.activity.SessionHolder;
import com.company.evernote_android.sync.processor.NotebookProcessor;
import com.company.evernote_android.sync.processor.ProcessorCallback;
import com.evernote.client.android.EvernoteSession;

/**
 * Created by Zalman on 12.04.2015.
 */
public class EvernoteService extends IntentService {

    public static final String INTENT_IDENTIFIER = "INTENT_IDENTIFIER";
    public static final String REQUEST_CALLBACK = "SERVICE_CALLBACK";
    public static final String REQUEST_TYPE = "REQUEST_TYPE";
    public static final String REQUEST_TYPE_NOTE = "NOTE";
    public static final String REQUEST_TYPE_NOTEBOOK = "NOTEBOOK";

    private Intent requestIntent;
    private ResultReceiver requestCallback;

    public EvernoteService() {
        super("EvernoteService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        requestIntent = intent;

        String requestType = requestIntent.getStringExtra(REQUEST_TYPE);
        requestCallback = requestIntent.getParcelableExtra(REQUEST_CALLBACK);


        EvernoteSession mEvernoteSession = EvernoteSession.getInstance(this,
                SessionHolder.CONSUMER_KEY,
                SessionHolder.CONSUMER_SECRET,
                SessionHolder.EVERNOTE_SERVICE,
                SessionHolder.SUPPORT_APP_LINKED_NOTEBOOKS
        );

        switch (requestType) {
            case REQUEST_TYPE_NOTEBOOK:
                NotebookProcessor notebookProcessor = new NotebookProcessor(getApplicationContext());
                notebookProcessor.getNotebooks(makeNoteProcessorCallback(), mEvernoteSession);
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
