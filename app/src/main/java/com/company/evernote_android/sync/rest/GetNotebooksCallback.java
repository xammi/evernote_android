package com.company.evernote_android.sync.rest;

import com.evernote.edam.type.Notebook;

import java.util.List;

/**
 * Created by Zalman on 17.05.2015.
 */
public interface GetNotebooksCallback {

    public void sendNotebooks(List<Notebook> notebooks);

}
