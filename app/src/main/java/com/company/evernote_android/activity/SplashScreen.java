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
import android.os.Bundle;
import android.os.Handler;

import java.io.IOException;

import com.company.evernote_android.R;
import com.company.evernote_android.auth.EvernoteAccount;
import com.company.evernote_android.provider.EvernoteContract;


public class SplashScreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        final Context context = SplashScreen.this;
        final AccountManager accountManager = AccountManager.get(this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Account[] accounts = accountManager.getAccountsByType(EvernoteAccount.TYPE);
                if (accounts.length == 0) {
                    addNewAccount(accountManager, context);
                }
                else {
                    Account account = accounts[0];
                    ContentResolver.requestSync(account, EvernoteContract.AUTHORITY, new Bundle());
                    MainActivity.startMainActivity(context);
                }
            }
        }, 2000);
    }

    private void addNewAccount(AccountManager am, final Context context) {
        am.addAccount(EvernoteAccount.TYPE, EvernoteAccount.TOKEN_FULL_ACCESS, null, null, this,
                new AccountManagerCallback<Bundle>() {
                    @Override
                    public void run(AccountManagerFuture<Bundle> future) {
                        try {
                            future.getResult();
                            MainActivity.startMainActivity(context);
                        } catch (OperationCanceledException | IOException | AuthenticatorException e) {
                            SplashScreen.this.finish();
                        }
                    }
                }, null);
    }
}