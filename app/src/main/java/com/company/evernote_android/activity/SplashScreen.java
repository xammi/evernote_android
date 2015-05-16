package com.company.evernote_android.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import java.io.IOException;

import com.company.evernote_android.R;
//import com.company.evernote_android.activity.auth.EvernoteAccount;
import com.company.evernote_android.activity.main.MainActivity;
import com.company.evernote_android.provider.EvernoteContract;


public class SplashScreen extends ParentActivity {

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

    public static void startMainActivity(Context ctx) {
        Intent intent = new Intent(ctx, MainActivity.class);
        ctx.startActivity(intent);
        ((Activity) ctx).finish();
    }
}