package com.company.evernote_android.utils;


import android.content.Context;

import com.evernote.client.android.EvernoteSession;

public class EvernoteSessionConstant {

    public static final String CONSUMER_KEY = "streambuf-8430";
    public static final String CONSUMER_SECRET = "e92a48e112672396";
    public static final EvernoteSession.EvernoteService EVERNOTE_SERVICE = EvernoteSession.EvernoteService.SANDBOX;
    public static final boolean SUPPORT_APP_LINKED_NOTEBOOKS = true;

    public static EvernoteSession getSession(Context context) {
        return EvernoteSession.getInstance(context,
                EvernoteSessionConstant.CONSUMER_KEY,
                EvernoteSessionConstant.CONSUMER_SECRET,
                EvernoteSessionConstant.EVERNOTE_SERVICE,
                EvernoteSessionConstant.SUPPORT_APP_LINKED_NOTEBOOKS
        );
    }
}
