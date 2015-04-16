package com.company.evernote_android.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by Zalman on 12.04.2015.
 */
public class EvernoteService extends IntentService {

    public EvernoteService() {
        super("EvernoteService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // get data from Intent

        // call processor method with callback


    }

    private NoteProcessorCallback makeNoteProcessorCallback() {
        NoteProcessorCallback callback = new NoteProcessorCallback() {
            @Override
            public void send(int resultCode) {
                // call callback Service Helper
            }
        };
        return callback;
    }

}
