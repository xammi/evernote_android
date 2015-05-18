package com.company.evernote_android.sync.rest.callback;

import com.company.evernote_android.utils.StatusCode;
import com.evernote.edam.type.Notebook;

import java.util.List;

/**
 * Created by Zalman on 17.05.2015.
 */
public interface SendNotebooksCallback {

    public void sendNotebooks(List<Notebook> notebooks, int statusCode);

}
