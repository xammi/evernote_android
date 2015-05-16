package com.company.evernote_android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.company.evernote_android.R;
//import com.company.evernote_android.activity.auth.EvernoteAccount;
import com.company.evernote_android.activity.main.MainActivity;
import com.evernote.client.android.EvernoteSession;


public class SplashScreen extends SessionHolder {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        final Context context = SplashScreen.this;

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