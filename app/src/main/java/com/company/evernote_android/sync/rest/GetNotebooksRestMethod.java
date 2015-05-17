package com.company.evernote_android.sync.rest;

import android.util.Log;

import com.company.evernote_android.utils.EvernoteSessionConstant;
import com.company.evernote_android.utils.StatusCode;
import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.OnClientCallback;
import com.evernote.edam.type.Notebook;
import com.evernote.thrift.transport.TTransportException;

import java.util.List;

/**
 * Created by Zalman on 16.05.2015.
 */
public class GetNotebooksRestMethod {

    private static String LOGTAG = "GetNotebooksRestMethod: ";

    public GetNotebooksRestMethod() {
    }

    public static void execute(final GetNotebooksCallback callback, final EvernoteSession mEvernoteSession) {

        if (mEvernoteSession.isLoggedIn()) {
            try {
                mEvernoteSession.getClientFactory().createNoteStoreClient().listNotebooks(new OnClientCallback<List<Notebook>>() {
                    @Override
                    public void onSuccess(final List<Notebook> notebooks) {
                        callback.sendNotebooks(notebooks, StatusCode.OK);
                    }

                    @Override
                    public void onException(Exception exception) {
                        callback.sendNotebooks(null, StatusCode.ERROR);
                    }
                });

            } catch (TTransportException exception) {
                Log.e(LOGTAG, "getNotebooks exception:", exception);
            }
        }
    }

}
