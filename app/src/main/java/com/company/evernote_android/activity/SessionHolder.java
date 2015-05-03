package com.company.evernote_android.activity;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import com.company.evernote_android.R;
import com.evernote.client.android.EvernoteSession;

public class SessionHolder extends ActionBarActivity {

    private static final String CONSUMER_KEY = "eugene07";
    private static final String CONSUMER_SECRET = "fe5beebef36a4335";

    private static final EvernoteSession.EvernoteService EVERNOTE_SERVICE = EvernoteSession.EvernoteService.SANDBOX;

    // Set this to true if you want to allow linked notebooks for accounts that can only access a single
    // notebook.
    private static final boolean SUPPORT_APP_LINKED_NOTEBOOKS = true;
    protected static EvernoteSession mEvernoteSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_holder);
        mEvernoteSession = EvernoteSession.getInstance(this, CONSUMER_KEY, CONSUMER_SECRET, EVERNOTE_SERVICE, SUPPORT_APP_LINKED_NOTEBOOKS);
    }
}
