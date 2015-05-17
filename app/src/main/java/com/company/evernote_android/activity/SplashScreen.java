package com.company.evernote_android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;

import com.company.evernote_android.R;
//import com.company.evernote_android.activity.auth.EvernoteAccount;
import com.company.evernote_android.activity.main.MainActivity;
import com.company.evernote_android.utils.EvernoteSessionConstant;
import com.evernote.client.android.EvernoteSession;


public class SplashScreen extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        final Context context = SplashScreen.this;

        final EvernoteSession mEvernoteSession = EvernoteSession.getInstance(this,
                EvernoteSessionConstant.CONSUMER_KEY,
                EvernoteSessionConstant.CONSUMER_SECRET,
                EvernoteSessionConstant.EVERNOTE_SERVICE,
                EvernoteSessionConstant.SUPPORT_APP_LINKED_NOTEBOOKS
        );

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (! mEvernoteSession.isLoggedIn()) {
                    mEvernoteSession.authenticate(context);
                }
                else {
                    startMainActivity(context);
                }
            }
        }, 2000);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case EvernoteSession.REQUEST_CODE_OAUTH:
                if (resultCode == Activity.RESULT_OK) {
                    startMainActivity(SplashScreen.this);
                }
                break;
        }
    }

    public static void startMainActivity(Context ctx) {
        Intent intent = new Intent(ctx, MainActivity.class);
        ctx.startActivity(intent);
        ((Activity) ctx).finish();
    }
}