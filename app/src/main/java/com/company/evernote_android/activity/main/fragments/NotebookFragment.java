package com.company.evernote_android.activity.main.fragments;


import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;

import static com.company.evernote_android.provider.EvernoteContract.*;

public class NotebookFragment extends NotesFragment {

    private static final String NOTEBOOK_ID_KEY = "notebook_id";
    private long notebookId;


    public static NotebookFragment newInstance(long notebookId) {
        NotebookFragment fragment = new NotebookFragment();
        Bundle args = new Bundle();
        args.putLong(NOTEBOOK_ID_KEY, notebookId);
        fragment.setArguments(args);
        return fragment;
    }

    public NotebookFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (getArguments() != null) {
            notebookId = getArguments().getLong(NOTEBOOK_ID_KEY);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String selection = Notes.STATE_DELETED + "=" + StateDeleted.FALSE.ordinal()
                + " AND " + Notes.NOTEBOOKS_ID + "=" + ((Long) notebookId).toString();

        return new CursorLoader(
                getActivity(),
                Notes.CONTENT_URI,
                Notes.ALL_COLUMNS_PROJECTION,
                selection,
                null,
                Notes.UPDATED + " DESC");
    }

}
