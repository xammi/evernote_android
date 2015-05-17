package com.company.evernote_android.activity;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.company.evernote_android.R;
import com.company.evernote_android.provider.ClientAPI;
import com.company.evernote_android.provider.DBService;
import static com.company.evernote_android.provider.EvernoteContract.*;
import com.evernote.client.android.EvernoteUtil;
import com.evernote.edam.type.Note;


public class NewNoteActivity extends ActionBarActivity {
    private static final String LOGTAG = "NewNoteActivity";

    protected EditText mEditTextTitle;
    protected EditText mEditTextContent;

    protected ClientAPI mService = null;
    protected long mSelectedNotebook = 1;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_note);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.drawable.ic_app_logo);
        toolbar.setTitleTextColor(getResources().getColor(R.color.green));
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);

        toolbar.setTitle(R.string.title_activity_new_note);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavUtils.navigateUpFromSameTask(NewNoteActivity.this);
            }
        });

        mEditTextTitle = (EditText) findViewById(R.id.text_title);
        mEditTextContent = (EditText) findViewById(R.id.text_content);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            DBService.DBWriteBinder binder = (DBService.DBWriteBinder)iBinder;
            mService = binder.getClientApiService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mService = null;
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        Intent intent = new Intent(this, DBService.class);
        this.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mService != null) {
            this.unbindService(serviceConnection);
        }
    }


    public void saveNote(View view) {
        String title = mEditTextTitle.getText().toString();
        String content = mEditTextContent.getText().toString();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(content)) {
            Toast.makeText(getApplicationContext(), R.string.empty_content_error, Toast.LENGTH_LONG).show();
            return;
        }

        Note note = new Note();
        note.setTitle(title);

        //TODO: line breaks need to be converted to render in ENML
        note.setContent(EvernoteUtil.NOTE_PREFIX + content + EvernoteUtil.NOTE_SUFFIX);
        boolean result = mService.insertNote(title.trim(), content, mSelectedNotebook);

        if (result) {
            Log.d(LOGTAG, "Note was saved");
            Toast.makeText(getApplicationContext(), R.string.note_saved, Toast.LENGTH_LONG).show();
        }
        else {
            Log.d(LOGTAG, "Error saving note");
            Toast.makeText(getApplicationContext(), R.string.error_saving_note, Toast.LENGTH_LONG).show();
        }
    }


    public void selectNotebook(View view) {
        Cursor cursor = mService.getAllNotebooks();
        int nameIndex = cursor.getColumnIndexOrThrow(Notebooks.NAME);
        int idIndex = cursor.getColumnIndexOrThrow(Notebooks._ID);

        if (cursor != null) {
            CharSequence[] names = new CharSequence[cursor.getCount()];
            final Long [] ids = new Long [cursor.getCount()];

            int I = 0;
            while (cursor.moveToNext()) {
                names[I] = cursor.getString(nameIndex);
                ids[I] = cursor.getLong(idIndex);
                I++;
            }
            int alreadySelected = EditNoteActivity.find(ids, mSelectedNotebook);
            if (alreadySelected == -1)
                alreadySelected = 1;

            AlertDialog.Builder builder = new AlertDialog.Builder(NewNoteActivity.this);
            builder.setSingleChoiceItems(names, alreadySelected, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mSelectedNotebook = ids[which];
                        }
                    });

            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

            builder.create().show();
        }
        else {
            Log.e(LOGTAG, "Error listing notebooks");
            Toast.makeText(getApplicationContext(), R.string.error_listing_notebooks, Toast.LENGTH_LONG).show();
        }
    }

    public static <T> int find(T [] array, T item) {
        if (item == null) return -1;

        for (int I = 0; I < array.length; I++) {
            if (item.equals(array[I])) {
                return I;
            }
        }
        return -1;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_new_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_save) {
            saveNote(null);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
