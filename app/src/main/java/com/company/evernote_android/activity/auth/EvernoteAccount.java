package com.company.evernote_android.activity.auth;

import android.accounts.Account;
import android.os.Bundle;
import android.os.Parcel;

import com.evernote.client.oauth.EvernoteAuthToken;

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

    public Bundle getUserData(EvernoteAuthToken evernoteAuthToken) {
        Bundle userdata = new Bundle();

        String noteStoreUrl = evernoteAuthToken.getNoteStoreUrl();
        String webApiUrlPrefix = evernoteAuthToken.getWebApiUrlPrefix();

        userdata.putString(EvernoteAccount.EXTRA_NOTE_STORE_URL, noteStoreUrl);
        userdata.putString(EvernoteAccount.EXTRA_WEB_API_URL_PREFIX, webApiUrlPrefix);
        userdata.putString(EvernoteAccount.EXTRA_LAST_SYNC_TIME, "0");
        userdata.putString(EvernoteAccount.EXTRA_LAST_UPDATED_COUNT, "0");

        return userdata;
    }
}