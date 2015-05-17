package com.company.evernote_android.sync.rest;

import com.evernote.edam.type.Note;

import java.util.List;

/**
 * Created by Zalman on 17.05.2015.
 */
public interface GetNotesCallback {

    public void sendNotes(List<Note> notebooks, int statusCode);

}
