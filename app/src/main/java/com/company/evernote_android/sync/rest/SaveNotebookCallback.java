package com.company.evernote_android.sync.rest;

import com.evernote.edam.type.Notebook;

/**
 * Created by Zalman on 18.05.2015.
 */
public interface SaveNotebookCallback {

    public void sendNotebook(Notebook notebook, int statusCode);

}
