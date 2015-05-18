package com.company.evernote_android.sync.rest;

import android.util.Log;

import com.company.evernote_android.sync.rest.callback.SendDataDeleteNoteCallback;
import com.company.evernote_android.utils.StatusCode;
import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.OnClientCallback;
import com.evernote.thrift.transport.TTransportException;

/**
 * Created by Zalman on 18.05.2015.
 */
public class DeleteNoteRestMethod {
    private static String LOGTAG = "DeleteNoteRestMethod: ";

    public DeleteNoteRestMethod() {
    }

    public static void execute(final SendDataDeleteNoteCallback callback, final EvernoteSession mEvernoteSession, final String guid) {

        if (mEvernoteSession.isLoggedIn()) {
            try {
                mEvernoteSession.getClientFactory().createNoteStoreClient().deleteNote(guid, new OnClientCallback<Integer>() {
                    @Override
                    public void onSuccess(Integer data) {
                        callback.sendInteger(data, guid, StatusCode.OK);
                    }

                    @Override
                    public void onException(Exception exception) {
                        callback.sendInteger(null, guid, StatusCode.ERROR);
                    }
                });
            } catch (TTransportException exception) {
                Log.e(LOGTAG, "deleteNote exception:", exception);
            }
        }
    }
}
