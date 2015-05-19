package com.company.evernote_android.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.company.evernote_android.R;
import com.company.evernote_android.activity.main.fragments.NotesFragment;
import com.company.evernote_android.provider.DBService;
import com.company.evernote_android.sync.EvernoteServiceHelper;
import com.company.evernote_android.utils.ParcelableNote;
import com.company.evernote_android.utils.StatusCode;
import com.evernote.client.android.EvernoteUtil;
import com.evernote.edam.type.Note;

import java.util.Date;

public class EditNoteActivity extends NewNoteActivity {
    private final String LOGTAG = "EditNoteActivity";

    private long noteId;
    private Note mNote = null;

    private EvernoteServiceHelper evernoteServiceHelper;
    private BroadcastReceiver broadcastReceiver;
    private long updateNoteRequestId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle(R.string.title_activity_edit_note);
        noteId = getIntent().getLongExtra(NotesFragment.NOTE_ID_KEY, 0);
        evernoteServiceHelper = EvernoteServiceHelper.getInstance(this);
        registerBroadcastReceiver();
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            DBService.DBWriteBinder binder = (DBService.DBWriteBinder)iBinder;
            mService = binder.getClientApiService();

            if (noteId != 0) {
                if (mService != null)
                    inflateNote();
            }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    private void inflateNote() {
        mNote = mService.getNote(noteId);

        if (mNote != null) {
            EditText title = (EditText) findViewById(R.id.text_title);
            title.setText(mNote.getTitle(), TextView.BufferType.EDITABLE);

            EditText content = (EditText) findViewById(R.id.text_content);
            content.setText(mNote.getContent(), TextView.BufferType.EDITABLE);

            mSelectedNotebook = mNote.getDeleted(); // feature
        }
        else {
            Toast.makeText(EditNoteActivity.this, R.string.err_retrieving_resource, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
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
        boolean result = mService.updateNote(noteId, title.trim(), content, mSelectedNotebook);

        if (result) {
            Note updatedNote = mService.getNote(noteId);
            if (updatedNote.getGuid() != null) {
                updateNoteRequestId = evernoteServiceHelper.updateNote(new ParcelableNote(updatedNote));
            }
            Log.d(LOGTAG, "Note was updated");
            Toast.makeText(getApplicationContext(), R.string.note_updated, Toast.LENGTH_LONG).show();
            finish();
        }
        else {
            Log.d(LOGTAG, "Error saving updated");
            Toast.makeText(getApplicationContext(), R.string.error_updating_note, Toast.LENGTH_LONG).show();
        }
    }

    private void registerBroadcastReceiver() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long resultRequestId = intent.getLongExtra(EvernoteServiceHelper.EXTRA_REQUEST_ID, -1);
                int resultCode = intent.getIntExtra(EvernoteServiceHelper.EXTRA_RESULT_CODE, 0);

                if (resultRequestId == updateNoteRequestId) {
                    showToast(resultCode, R.string.sync_note, R.string.sync_error_note);
                }

            }
        };

        IntentFilter filter = new IntentFilter(EvernoteServiceHelper.ACTION_REQUEST_RESULT);
        registerReceiver(broadcastReceiver, filter);
    }

    private void showToast(int statusCode, int messageOk, int messageError) {
        int message;
        if (statusCode == StatusCode.OK) {
            message = messageOk;
        } else {
            message = messageError;
        }
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }
}
