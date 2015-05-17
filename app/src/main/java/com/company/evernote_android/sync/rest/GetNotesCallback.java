package com.company.evernote_android.sync.rest;

import com.evernote.edam.type.Note;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Zalman on 17.05.2015.
 */
public interface GetNotesCallback {

    public void sendNotes(ConcurrentLinkedQueue<Note> notebooks, int statusCode);

}
