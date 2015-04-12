package com.company.evernote_android.service;

import android.content.Context;
import android.content.Intent;

import java.util.UUID;

/**
 * Created by Zalman on 12.04.2015.
 */
public class EvernoteServiceHelper {

    private static Object lock = new Object();
    private Context appContext;
    private static EvernoteServiceHelper instance;

    public EvernoteServiceHelper(Context context) {
        this.appContext = context;
    }

    private static EvernoteServiceHelper getInstance(Context appContext) {
        synchronized (lock) {
            if(instance == null) {
                instance = new EvernoteServiceHelper(appContext);
            }
        }
        return instance;
    }

    // TODO
    public long getNotes() {

        long requestId = generateRequestID();

        Intent intent = new Intent(appContext, EvernoteService.class);
        intent.putExtra("REQUEST_ID", requestId);

        appContext.startService(intent);


        return requestId;
    }

    private long generateRequestID() {
        long requestId = UUID.randomUUID().getLeastSignificantBits();
        return requestId;
    }
}
