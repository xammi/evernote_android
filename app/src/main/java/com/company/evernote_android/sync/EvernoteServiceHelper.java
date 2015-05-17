package com.company.evernote_android.sync;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Zalman on 12.04.2015.
 */
public class EvernoteServiceHelper {

    private static final String REQUEST_ID = "REQUEST_ID";
    public static String ACTION_REQUEST_RESULT = "REQUEST_RESULT";
    public static String EXTRA_REQUEST_ID = "EXTRA_REQUEST_ID";
    public static String EXTRA_RESULT_CODE = "EXTRA_RESULT_CODE";

    private static Object lock = new Object();
    private Context context;
    private static EvernoteServiceHelper instance;
    private Map<String,Long> pendingRequests = new HashMap<String,Long>();

    private EvernoteServiceHelper(Context context) {
        this.context = context;
    }

    public static EvernoteServiceHelper getInstance(Context context) {
        synchronized (lock) {
            if(instance == null) {
                instance = new EvernoteServiceHelper(context);
            }
        }
        return instance;
    }

    public long getAllNotes(int maxNotesInNotebook) {

        Result result = makeRequest(EvernoteService.TYPE_GET_NOTES);

        if (result.isPending()) {
            return result.getRequestId();
        }

        Intent intent = result.getIntent();
        intent.putExtra("maxNotes", maxNotesInNotebook);
        context.startService(intent);

        return result.getRequestId();
    }

    public long getNotebooks() {

        Result result = makeRequest(EvernoteService.TYPE_GET_NOTEBOOKS);

        if (result.isPending()) {
            return result.getRequestId();
        }

        Intent intent = result.getIntent();
        context.startService(intent);

        return result.getRequestId();
    }


    private Result makeRequest(String type_request) {

        if(pendingRequests.containsKey(type_request)){
            return new Result(pendingRequests.get(type_request), null, true);
        }

        long requestId = generateRequestID();
        pendingRequests.put(type_request, requestId);

        Intent intent = createIntent(requestId);
        intent.putExtra(EvernoteService.ACTION_TYPE, type_request);
        return new Result(requestId, intent, false);
    }

    private void handleResponce(int resultCode, Bundle resultData) {
        Intent intent = (Intent)resultData.getParcelable(EvernoteService.INTENT_IDENTIFIER);
        if (intent != null) {
            long requestId = intent.getLongExtra(REQUEST_ID, 0);
            String requestType = intent.getStringExtra(EvernoteService.ACTION_TYPE);

            switch (requestType) {
                case EvernoteService.TYPE_GET_NOTEBOOKS:
                    pendingRequests.remove(EvernoteService.TYPE_GET_NOTEBOOKS);
                    break;
                case EvernoteService.TYPE_GET_NOTES:
                    pendingRequests.remove(EvernoteService.TYPE_GET_NOTES);
                    break;
            }

            Intent resultBroadcast = new Intent(ACTION_REQUEST_RESULT);
            resultBroadcast.putExtra(EXTRA_REQUEST_ID, requestId);
            resultBroadcast.putExtra(EXTRA_RESULT_CODE, resultCode);

            context.sendBroadcast(resultBroadcast);
        }
    }

    private Intent createIntent(long requestId) {
        ResultReceiver serviceCallback = new ResultReceiver(null){

            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                handleResponce(resultCode, resultData);
            }

        };

        Intent intent = new Intent(context, EvernoteService.class);
        intent.putExtra(REQUEST_ID, requestId);
        intent.putExtra(EvernoteService.REQUEST_CALLBACK, serviceCallback);

        return intent;
    }

    private long generateRequestID() {
        long requestId = UUID.randomUUID().getLeastSignificantBits();
        return requestId;
    }


    private class Result {
        private long requestId;
        private Intent intent;
        private boolean isPending;

        private Result(long requestId, Intent intent, boolean isPending) {
            this.requestId = requestId;
            this.intent = intent;
            this.isPending = isPending;
        }

        public long getRequestId() {
            return requestId;
        }

        public Intent getIntent() {
            return intent;
        }

        public boolean isPending() {
            return isPending;
        }
    }
}
