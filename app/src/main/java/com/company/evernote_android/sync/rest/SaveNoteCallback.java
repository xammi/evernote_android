package com.company.evernote_android.sync.rest;


import com.evernote.edam.type.Note;

/**
 * Created by Zalman on 18.05.2015.
 */
public interface SaveNoteCallback {

    public void sendNote(Note note, int statusCode);

}
