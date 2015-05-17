package com.company.evernote_android.activity.main.fragments;


import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.company.evernote_android.R;
import com.company.evernote_android.activity.ReadNoteActivity;

import static com.company.evernote_android.provider.EvernoteContract.*;


public class NotesFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private CursorAdapter mAdapter;

    private final String[] from = new String[]{Notes.TITLE, Notes.CONTENT};
    private final int[] to = new int[]{R.id.title, R.id.content};

    public NotesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLoaderManager().initLoader(0, null, this);

        mAdapter = new SimpleCursorAdapter(getActivity(), R.layout.note_item, null, from, to, 0);
        setListAdapter(mAdapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent intent = new Intent(getActivity(), ReadNoteActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(
                getActivity(),
                Notes.CONTENT_URI,
                Notes.ALL_COLUMNS_PROJECTION,
                Notebooks.STATE_DELETED + "=" + StateDeleted.FALSE.ordinal(),
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mAdapter.swapCursor(cursor);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mAdapter.swapCursor(null);
    }
}
