package com.company.evernote_android.activity;

import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;

import com.company.evernote_android.R;
import com.evernote.client.android.EvernoteSession;


public class LoginActivity extends ActionBarActivity implements LoaderCallbacks<Cursor> {

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
        setContentView(R.layout.activity_login);
        mEvernoteSession = EvernoteSession.getInstance(this,
                CONSUMER_KEY,
                CONSUMER_SECRET,
                EVERNOTE_SERVICE,
                SUPPORT_APP_LINKED_NOTEBOOKS
        );
        mEvernoteSession.authenticate(LoginActivity.this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            //Update UI when oauth activity returns result
            case EvernoteSession.REQUEST_CODE_OAUTH:
                if (resultCode == Activity.RESULT_OK) {
                    String OauthToken = data.getStringExtra("oauth_token");
                    Toast toast = Toast.makeText(getApplicationContext(), "Authorized " + OauthToken, Toast.LENGTH_LONG);
                    toast.show();
                }
                break;
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}