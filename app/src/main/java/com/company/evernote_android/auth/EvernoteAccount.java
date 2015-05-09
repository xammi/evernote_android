package com.company.evernote_android.auth;

import android.accounts.Account;
import android.os.Parcel;

/**
 * Created by max on 09.05.201.
 */
public class EvernoteAccount extends Account {

    public static final String TYPE = "com.company.evernote_android.auth";
    public static final String TOKEN_FULL_ACCESS = "com.company.evernote_android.auth.FULL_ACCESS";

    public static final String EXTRA_NOTE_STORE_URL = "com.company.evernote_android.extra.NOTE_STORE_URL";
    public static final String EXTRA_WEB_API_URL_PREFIX = "com.company.evernote_android.extra.WEB_API_URL_PREFIX";
    public static final String EXTRA_LAST_UPDATED_COUNT = "com.company.evernote_android.extra.LAST_UPDATED_COUNT";
    public static final String EXTRA_LAST_SYNC_TIME = "com.company.evernote_android.extra.LAST_SYNC_TIME";

    public EvernoteAccount(String name) {
        super(name, TYPE);
    }

    public EvernoteAccount(Parcel in) {
        super(in);
    }
}