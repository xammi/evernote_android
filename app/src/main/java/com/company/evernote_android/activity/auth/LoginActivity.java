package com.company.evernote_android.activity.auth;

import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.company.evernote_android.sync.ClientWrapper;
import com.evernote.client.android.EvernoteSession;
import com.evernote.client.oauth.EvernoteAuthToken;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.EvernoteApi;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;
import org.xmlpull.v1.XmlPullParserException;

import com.company.evernote_android.R;
import com.evernote.edam.error.EDAMNotFoundException;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.edam.type.User;
import com.evernote.thrift.TException;

import java.io.IOException;

/**
 * Created by max on 09.05.2015.
 */
public class LoginActivity extends AccountAuthenticatorActivity {
    private static final String LOGTAG = LoginActivity.class.getSimpleName();

    public static final String EXTRA_TOKEN_TYPE = "EXTRA_TOKEN_TYPE";
    private static final String HOST = EvernoteSession.HOST_SANDBOX;

    private static final String CONSUMER_KEY = "eugene07";
    private static final String CONSUMER_SECRET = "fe5beebef36a4335";
    private static final boolean SUPPORT_APP_LINKED_NOTEBOOKS = true;


    private static final String CALLBACK_SCHEME = "bmstu-oauth";

    private String mRequestToken = null;
    private String mRequestTokenSecret = null;

    private WebView mWebView;

    private AsyncTask mBeginAuthSyncTask = null;
    private AsyncTask mCompleteAuthSyncTask = null;

    private WebViewClient mWebViewClient = new WebViewClient() {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Uri uri = Uri.parse(url);

            if (uri.getScheme().equals(CALLBACK_SCHEME)) {
                if (mCompleteAuthSyncTask == null) {
                    mCompleteAuthSyncTask = new CompleteAuthAsyncTask().execute(uri);
                }
                return true;
            }
            return super.shouldOverrideUrlLoading(view, url);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mWebView = (WebView) findViewById(R.id.webview);
        mWebView.setWebViewClient(mWebViewClient);
        mWebView.getSettings().setJavaScriptEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mBeginAuthSyncTask == null)
            mBeginAuthSyncTask = new BootstrapAsyncTask().execute();
    }

    private OAuthService createService() {

        OAuthService builder = null;
        Class apiClass = null;

        if (HOST.equals(EvernoteSession.HOST_SANDBOX)) {
            apiClass = EvernoteApi.Sandbox.class;
        }
        else if (HOST.equals(EvernoteSession.HOST_PRODUCTION)) {
            apiClass = EvernoteApi.class;
        }
        else {
            throw new IllegalArgumentException("Unsupported Evernote host: " + HOST);
        }
        builder = new ServiceBuilder()
                .provider(apiClass)
                .apiKey(CONSUMER_KEY)
                .apiSecret(CONSUMER_SECRET)
                .callback(CALLBACK_SCHEME + "://callback")
                .build();
        return builder;
    }

    private void onAuthTokenReceived(EvernoteAuthToken evernoteAuthToken) {
        try {
            ClientWrapper clientWrapper = new ClientWrapper(LoginActivity.this);
            User user = clientWrapper.getUserStoreClient().getUser(evernoteAuthToken.getToken());

            String username = "max";

            String token = evernoteAuthToken.getToken();
            EvernoteAccount account = new EvernoteAccount(username);
            AccountManager accountManager = AccountManager.get(this);

            final Bundle result = new Bundle();
            Bundle userdata = account.getUserData(evernoteAuthToken);

            if (accountManager.addAccountExplicitly(account, null, userdata)) {
                result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
                result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
                result.putString(AccountManager.KEY_AUTHTOKEN, token);

                accountManager.setAuthToken(account, account.type, token);
                ContentResolver.setMasterSyncAutomatically(true);
            } else {
                result.putString(AccountManager.KEY_ERROR_MESSAGE, "Failed to add user");
            }
            setAccountAuthenticatorResult(result);
            setResult(RESULT_OK);
            finish();

        } catch (EDAMSystemException | TException | EDAMUserException e) {
            e.printStackTrace();
        }
    }

    private class BootstrapAsyncTask extends AsyncTask<Void, Void, String> {
        ProgressDialog progressDialog = null;

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(LoginActivity.this, "Loading", "Request token loading");
        }

        @Override
        protected String doInBackground(Void... params) {
            String url = null;
            try {
                OAuthService service = createService();

                Log.i(LOGTAG, "Retrieving OAuth request token...");
                Token reqToken = service.getRequestToken();
                mRequestToken = reqToken.getToken();
                mRequestTokenSecret = reqToken.getSecret();

                Log.i(LOGTAG, "Redirecting user for authorization...");
                url = service.getAuthorizationUrl(reqToken);
            }
            catch (Exception ex) {
                Log.e(LOGTAG, "Failed to obtain OAuth request token", ex);
            }
            return url;
        }

        @Override
        protected void onPostExecute(String url) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            mWebView.loadUrl(url);
        }
    }

    private class CompleteAuthAsyncTask extends AsyncTask<Uri, Void, EvernoteAuthToken> {
        ProgressDialog progressDialog = null;

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(LoginActivity.this, "Loading", "Authentication token loading");
        }

        @Override
        protected EvernoteAuthToken doInBackground(Uri... uris) {
            EvernoteAuthToken evernoteAuthToken = null;
            if (uris == null || uris.length == 0) {
                return null;
            }
            Uri uri = uris[0];

            if (!TextUtils.isEmpty(mRequestToken)) {
                OAuthService service = createService();
                String verifierString = uri.getQueryParameter("oauth_verifier");

                if (TextUtils.isEmpty(verifierString)) {
                    Log.i(LOGTAG, "User did not authorize access");
                }
                else {
                    Verifier verifier = new Verifier(verifierString);
                    Log.i(LOGTAG, "Retrieving OAuth access token...");

                    try {
                        Token reqToken = new Token(mRequestToken, mRequestTokenSecret);
                        Token authToken = service.getAccessToken(reqToken, verifier);
                        evernoteAuthToken = new EvernoteAuthToken(authToken, SUPPORT_APP_LINKED_NOTEBOOKS);
                    }
                    catch (Exception ex) {
                        Log.e(LOGTAG, "Failed to obtain OAuth access token", ex);
                    }
                }
            }
            else {
                Log.d(LOGTAG, "Unable to retrieve OAuth access token, no request token");
            }
            return evernoteAuthToken;
        }

        /**
         * Save the authentication information resulting from a successful
         * OAuth authorization and complete the activity.
         */
        @Override
        protected void onPostExecute(EvernoteAuthToken evernoteAuthToken) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            onAuthTokenReceived(evernoteAuthToken);
        }
    }
}