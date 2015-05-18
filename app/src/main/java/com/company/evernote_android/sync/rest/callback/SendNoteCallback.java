package com.company.evernote_android.sync.rest.callback;


import com.evernote.edam.type.Note;

/**
 * Created by Zalman on 18.05.2015.
 */
public interface SendNoteCallback {

    public void sendNote(Note note, int statusCode);

}
