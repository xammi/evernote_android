package com.company.evernote_android.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import com.company.evernote_android.activity.auth.EvernoteAccount;
import com.evernote.client.conn.mobile.TEvernoteHttpClient;
import com.evernote.edam.notestore.NoteStore;
import com.evernote.edam.userstore.UserStore;
import com.evernote.thrift.protocol.TBinaryProtocol;
import com.evernote.thrift.protocol.TProtocol;
import com.evernote.thrift.transport.TTransport;
import com.evernote.thrift.transport.TTransportException;

import java.io.File;
import java.util.Locale;

/**
 * Created by max on 16.05.15.
 */
public class ClientWrapper {

    private static final String LOGTAG = "EvernoteHelper";

    private String mUserAgent;
    private File mTempDir;
    private String noteStoreUrl;

    public ClientWrapper(Context context) {
        mUserAgent = generateUserAgentString(context);
        mTempDir = context.getFilesDir();

        AccountManager accountManager = AccountManager.get(context);
        Account[] accounts = accountManager.getAccountsByType(EvernoteAccount.TYPE);
        if (accounts.length != 0) {
            Account account = accounts[0];
            this.noteStoreUrl = accountManager.getUserData(account, EvernoteAccount.EXTRA_NOTE_STORE_URL);
        }
    }

    public UserStore.Client getUserStoreClient() throws TTransportException {
        TTransport transport = new TEvernoteHttpClient("https://sandbox.evernote.com/edam/user", mUserAgent, mTempDir);
        TProtocol protocol = new TBinaryProtocol(transport);
        return new UserStore.Client(protocol);
    }

    public NoteStore.Client getNoteStoreClient() throws TTransportException {
        TTransport transport = new TEvernoteHttpClient(noteStoreUrl, mUserAgent, mTempDir);
        TProtocol protocol = new TBinaryProtocol(transport);
        return new NoteStore.Client(protocol);
    }

    private String generateUserAgentString(Context ctx) {
        // com.evernote.sample Android/216817 (en); Android/4.0.3; Xoom/15;"

        String packageName = null;
        int packageVersion = 0;
        try {
            packageName= ctx.getPackageName();
            packageVersion = ctx.getPackageManager().getPackageInfo(packageName, 0).versionCode;

        } catch (PackageManager.NameNotFoundException e) {
            Log.e(LOGTAG, e.getMessage());
        }

        String userAgent = packageName+ " Android/" +packageVersion;

        Locale locale = java.util.Locale.getDefault();
        if (locale == null) {
            userAgent += " ("+Locale.US+");";
        } else {
            userAgent += " (" + locale.toString()+ "); ";
        }
        userAgent += "Android/"+ Build.VERSION.RELEASE+"; ";
        userAgent +=
                Build.MODEL + "/" + Build.VERSION.SDK_INT + ";";
        return userAgent;
    }
}
