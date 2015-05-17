package com.company.evernote_android.sync;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;

import com.evernote.client.android.EvernoteSession;

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

    // TODO
    public long getNotes() {

        // find this method in queue

        long requestId = generateRequestID();

        Intent intent = new Intent(context, EvernoteService.class);
        intent.putExtra("REQUEST_ID", requestId);

        // create callback

        // put callback in intent

        context.startService(intent);


        return requestId;
    }

    // TODO
    public long getNotebooks() {

        // find this method in queue

        long requestId = generateRequestID();



        ResultReceiver serviceCallback = new ResultReceiver(null){

            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                handleNotebooksResponce(resultCode, resultData);
            }

        };

        Intent intent = new Intent(context, EvernoteService.class);
        intent.putExtra(REQUEST_ID, requestId);
        intent.putExtra(EvernoteService.REQUEST_CALLBACK, serviceCallback);
        intent.putExtra(EvernoteService.REQUEST_TYPE, EvernoteService.REQUEST_TYPE_NOTEBOOK);
        //intent.putExtra(EvernoteService.REQUEST_TYPE, context);

        context.startService(intent);


        return requestId;
    }

    //TODO
    public void handleNoteResponce(int resultCode) {


        // call callback or send broadcast?
    }

    public void handleNotebooksResponce(int resultCode, Bundle resultData) {

        Intent intent = (Intent)resultData.getParcelable(EvernoteService.INTENT_IDENTIFIER);

        if (intent != null) {
            long requestId = intent.getLongExtra(REQUEST_ID, 0);

            Intent resultBroadcast = new Intent(ACTION_REQUEST_RESULT);
            resultBroadcast.putExtra(EXTRA_REQUEST_ID, requestId);
            resultBroadcast.putExtra(EXTRA_RESULT_CODE, resultCode);

            context.sendBroadcast(resultBroadcast);
        }


    }

    private long generateRequestID() {
        long requestId = UUID.randomUUID().getLeastSignificantBits();
        return requestId;
    }
}
