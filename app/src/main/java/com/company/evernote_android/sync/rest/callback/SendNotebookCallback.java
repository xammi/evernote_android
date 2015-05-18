package com.company.evernote_android.sync.rest.callback;

import com.evernote.edam.type.Notebook;

/**
 * Created by Zalman on 18.05.2015.
 */
public interface SendNotebookCallback {

    public void sendNotebook(Notebook notebook, int statusCode);

}
