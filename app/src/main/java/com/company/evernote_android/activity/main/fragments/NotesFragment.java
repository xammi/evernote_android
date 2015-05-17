package com.company.evernote_android.activity.main.fragments;


import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.company.evernote_android.R;
import com.company.evernote_android.activity.ReadNoteActivity;

import java.util.Date;

import static com.company.evernote_android.provider.EvernoteContract.*;


public class NotesFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private CursorAdapter mAdapter;

    public final static String NOTE_ID_KEY = "note_id";

    private final String[] from = new String[] {Notes.TITLE, Notes.CONTENT, Notes.UPDATED};
    private final int[] to = new int[] {R.id.title, R.id.content, R.id.date};

    public NotesFragment() {
        // Required empty public constructor
    }

    class CustomAdapter extends SimpleCursorAdapter {

        public CustomAdapter(Context context, int layout, Cursor c, String[] from,
                                   int[] to, int flags) {
            super(context, layout, c, from, to, flags);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            super.bindView(view, context, cursor);

            TextView content = (TextView) view.findViewById(R.id.content);
            int contentIndex = cursor.getColumnIndexOrThrow(Notes.CONTENT);
            String contentString = cursor.getString(contentIndex);

            if (contentString.length() > 30) {
                contentString = contentString.substring(0, 30) + "...";
            }
            content.setText(contentString);

            TextView date = (TextView) view.findViewById(R.id.date);
            int dateIndex = cursor.getColumnIndexOrThrow(Notes.UPDATED);
            String dateString = cursor.getString(dateIndex);
            date.setText(new Date(Long.parseLong(dateString)).toString());
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLoaderManager().initLoader(0, null, this);
        mAdapter = new CustomAdapter(getActivity(), R.layout.note_item, null, from, to, 0);
        setListAdapter(mAdapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent intent = new Intent(getActivity(), ReadNoteActivity.class);
        intent.putExtra(NOTE_ID_KEY, id);
        startActivity(intent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(
                getActivity(),
                Notes.CONTENT_URI,
                Notes.ALL_COLUMNS_PROJECTION,
                Notes.STATE_DELETED + "=" + StateDeleted.FALSE.ordinal(),
                null,
                Notes.UPDATED + " DESC");
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
