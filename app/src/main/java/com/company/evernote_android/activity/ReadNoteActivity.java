package com.company.evernote_android.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.company.evernote_android.R;
import com.company.evernote_android.activity.main.fragments.NotesFragment;
import com.company.evernote_android.provider.ClientAPI;
import com.company.evernote_android.provider.DBService;
import com.evernote.edam.type.Note;

import java.util.Date;


public class ReadNoteActivity extends ActionBarActivity {
    private static final String LOGTAG = "ReadNoteActivity";

    private long noteId;
    private Note mNote = null;
    private ClientAPI mService;

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

    private void inflateNote() {
        mNote = mService.getNote(noteId);

        if (mNote != null) {
            TextView title = (TextView) findViewById(R.id.title);
            title.setText(mNote.getTitle());

            TextView content = (TextView) findViewById(R.id.content);
            content.setText(mNote.getContent());

            TextView date = (TextView) findViewById(R.id.date);
            date.setText(new Date(mNote.getUpdated()).toString());
        }
        else {
            Toast.makeText(ReadNoteActivity.this, R.string.err_retrieving_resource, Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteNote() {
        boolean deleted = mService.deleteNote(noteId);
        if (deleted) {
            finish();
        }
        else {
            Toast.makeText(ReadNoteActivity.this, R.string.error_deleting_note, Toast.LENGTH_SHORT).show();
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
}
