package com.company.evernote_android.activity.main;

import com.company.evernote_android.provider.EvernoteContract;

/**
 * Created by max on 17.05.15.
 */
public class NotebookMenuItem extends SlideMenuItem {
    private long notebookId;

    public NotebookMenuItem(String text, int iconId, long notebookId) {
        super(text, iconId);
        this.notebookId = notebookId;
    }

    public long getId() {
        return notebookId;
    }

    @Override
    public String type() {
        return EvernoteContract.Notebooks.CONTENT_TYPE;
    }
}
