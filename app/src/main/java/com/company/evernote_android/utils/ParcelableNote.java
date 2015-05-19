package com.company.evernote_android.utils;

import android.os.Parcel;
import android.os.Parcelable;

import com.evernote.client.android.EvernoteUtil;
import com.evernote.edam.type.Note;

/**
 * Created by Zalman on 18.05.2015.
 */
public class ParcelableNote implements Parcelable {

    private String title;
    private String content;
    private String guid;
    private String notebookGuid;
    private long created;
    private long updated;
    private long noteId;

    private ParcelableNote(Parcel parcel) {
        title = parcel.readString();
        content = parcel.readString();
        guid = parcel.readString();
        notebookGuid = parcel.readString();
        created = parcel.readLong();
        updated = parcel.readLong();
        noteId = parcel.readLong();
    }

    public ParcelableNote() {
    }

    public ParcelableNote(String title, String content, String notebookGuid, long created, long noteId) {
        this.title = title;
        this.content = content;
        this.notebookGuid = notebookGuid;
        this.created = created;
        this.noteId = noteId;
    }

    public ParcelableNote(Note note) {
        title = note.getTitle();
        content = note.getContent();
        guid = note.getGuid();
        notebookGuid = note.getNotebookGuid();
        created = note.getCreated();
        updated = note.getUpdated();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(content);
        dest.writeString(guid);
        dest.writeString(notebookGuid);
        dest.writeLong(created);
        dest.writeLong(updated);
        dest.writeLong(noteId);
    }

    public static final Creator<ParcelableNote> CREATOR = new Parcelable.Creator<ParcelableNote>() {

        @Override
        public ParcelableNote createFromParcel(Parcel source) {
            return new ParcelableNote(source);
        }

        @Override
        public ParcelableNote[] newArray(int size) {
            return new ParcelableNote[0];
        }
    };

    public Note toNote() {
        Note note = new Note();
        note.setTitle(title);
        note.setContent(EvernoteUtil.NOTE_PREFIX + content + EvernoteUtil.NOTE_SUFFIX);
        note.setGuid(guid);
        note.setNotebookGuid(notebookGuid);
        note.setCreated(created);
        note.setUpdated(updated);
        return note;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getNotebookGuid() {
        return notebookGuid;
    }

    public void setNotebookGuid(String notebookGuid) {
        this.notebookGuid = notebookGuid;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public long getUpdated() {
        return updated;
    }

    public void setUpdated(long updated) {
        this.updated = updated;
    }

    public long getNoteId() {
        return noteId;
    }

    public void setNoteId(long noteId) {
        this.noteId = noteId;
    }
}
