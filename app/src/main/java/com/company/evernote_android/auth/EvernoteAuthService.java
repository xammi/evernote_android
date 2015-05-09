package com.company.evernote_android.auth;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.company.evernote_android.auth.EvernoteAuthenticator;

/**
 * Created by max on 09.05.2015.
 */
public class EvernoteAuthService extends Service {
    private EvernoteAuthenticator mEvernoteAuthenticator;

    @Override
    public void onCreate() {
        super.onCreate();
        mEvernoteAuthenticator = new EvernoteAuthenticator(getApplicationContext());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mEvernoteAuthenticator.getIBinder();
    }
}