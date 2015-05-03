package com.company.evernote_android.activity;

import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;

import android.os.Bundle;
import android.widget.Toast;

import com.company.evernote_android.R;
import com.evernote.client.android.EvernoteSession;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends SessionHolder implements LoaderCallbacks<Cursor> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
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