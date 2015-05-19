package com.company.evernote_android.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.company.evernote_android.R;
import com.company.evernote_android.activity.main.fragments.NotesFragment;
import com.company.evernote_android.provider.ClientAPI;
import com.company.evernote_android.provider.DBService;
import com.company.evernote_android.sync.EvernoteServiceHelper;
import com.company.evernote_android.utils.StatusCode;
import com.evernote.edam.type.Note;
import com.evernote.edam.type.Notebook;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ReadNoteActivity extends ActionBarActivity {
    private static final String LOGTAG = "ReadNoteActivity";

    private long noteId;
    private Note mNote = null;
    private ClientAPI mService;
    private EvernoteServiceHelper evernoteServiceHelper;
    private BroadcastReceiver broadcastReceiver;
    private long deleteNoteRequestId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_note);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.drawable.ic_app_logo);
        toolbar.setTitleTextColor(getResources().getColor(R.color.green));
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);

        toolbar.setTitle(R.string.title_activity_read_note);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavUtils.navigateUpFromSameTask(ReadNoteActivity.this);
            }
        });

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
            TextView titleView = (TextView) findViewById(R.id.title);
            titleView.setText(mNote.getTitle());

            TextView contentView = (TextView) findViewById(R.id.content);
            contentView.setText(mNote.getContent());

            TextView dateView = (TextView) findViewById(R.id.date);
            dateView.setText(new SimpleDateFormat("dd.MM.yyyy").format(new Date(mNote.getUpdated())));

            Notebook notebook = mService.getNotebook(mNote.getDeleted()); // feature
            TextView notebookView = (TextView) findViewById(R.id.notebook);

            if (notebook != null) {
                notebookView.setText("Блокнот: " + notebook.getName());
            }
            else {
                notebookView.setText("Без блокнота");
            }
        }
        else {
            Toast.makeText(ReadNoteActivity.this, R.string.err_retrieving_resource, Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteNote() {
        if (mService.deleteNote(noteId)) {
            finish();
        }
        else {
            Toast.makeText(ReadNoteActivity.this, R.string.error_deleting_note, Toast.LENGTH_SHORT).show();
        }
        // TODO getNote(noteId) возвращает note с guid = null из-за этого синронизироваться не будет
        String guid = mService.getNote(noteId).getGuid();
        if (guid != null) {
            deleteNoteRequestId = evernoteServiceHelper.deleteNote(guid);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_read_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_discard) {
            deleteNote();
            return true;
        }
        else if (id == R.id.action_edit) {
            Intent intent = new Intent(ReadNoteActivity.this, EditNoteActivity.class);
            intent.putExtra(NotesFragment.NOTE_ID_KEY, noteId);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void registerBroadcastReceiver() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long resultRequestId = intent.getLongExtra(EvernoteServiceHelper.EXTRA_REQUEST_ID, -1);
                int resultCode = intent.getIntExtra(EvernoteServiceHelper.EXTRA_RESULT_CODE, 0);

                if (resultRequestId == deleteNoteRequestId) {
                    showToast(resultCode, R.string.sync_deleting_note, R.string.sync_error_deleting_note);
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
