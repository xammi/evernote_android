package com.company.evernote_android.activity.main;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;

import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.os.IBinder;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import android.widget.Toast;

import com.company.evernote_android.R;
import com.company.evernote_android.activity.NewNoteActivity;

import com.company.evernote_android.activity.main.fragments.NotImplemented;
import com.company.evernote_android.activity.main.fragments.NotebookFragment;

import com.company.evernote_android.provider.ClientAPI;
import com.company.evernote_android.provider.DBService;
import static com.company.evernote_android.provider.EvernoteContract.*;
import com.company.evernote_android.utils.EvernoteSessionConstant;

import com.company.evernote_android.activity.main.fragments.NotesFragment;

import com.company.evernote_android.sync.EvernoteServiceHelper;

import com.company.evernote_android.utils.ParcelableNote;
import com.company.evernote_android.utils.StatusCode;
import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.InvalidAuthenticationException;
import com.evernote.edam.type.Note;


import android.database.SQLException;
import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    private final static String LOGTAG = "MainActivity";

    private DrawerLayout drawerLayout;
    private ListView slideMenu;
    private ActionBarDrawerToggle drawerToggle;

    // nav drawer title
    private CharSequence appTitle;
    private ArrayList<String> slideMenuTitles = null;
    private ArrayList<SlideMenuItem> slideMenuItems = null;

    private EvernoteServiceHelper evernoteServiceHelper;
    private long notebooksRequestId;
    private long notesRequestId;
    private long saveNotebookRequestId;
    private ClientAPI mService = null;

    private BroadcastReceiver broadcastReceiver;
    private boolean showSyncMessageFlag = false;

    ProgressDialog pd;
    ImageButton FAB;

    private class SlideMenuClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (position == 1) return;
            displayView(position);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appTitle = getTitle();
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        slideMenu = (ListView) findViewById(R.id.slide_menu);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.drawable.ic_app_logo);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));

        evernoteServiceHelper = EvernoteServiceHelper.getInstance(this);
        //syncNotebooksAndNotes();
        registerBroadcastReceiver();

        FAB = (ImageButton) findViewById(R.id.imageButton);
        FAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NewNoteActivity.class);
                startActivity(intent);
            }
        });

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.app_name, R.string.app_name)
        {
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(appTitle);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle("");
                invalidateOptionsMenu();
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);

        pd = new ProgressDialog(this);
        pd.setTitle("");
        pd.setMessage(getResources().getString(R.string.esdk__loading));
    }

    private void displayView(int position) {
        setTitle(slideMenuTitles.get(position));
        drawerLayout.closeDrawer(slideMenu);

        // update the main content by replacing fragments
        Fragment fragment = null;
        if (position == 0) {
            fragment = new NotesFragment();
        }
        else if (Notebooks.CONTENT_TYPE.equals(slideMenuItems.get(position).type())) {
            SlideMenuItem item = slideMenuItems.get(position);
            fragment = NotebookFragment.newInstance(((NotebookMenuItem) item).getId());
        }
        else {
            fragment = new NotImplemented();
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.list_frame, fragment).commit();
        }
    }

    @Override
    public void onResume() {
        if (slideMenuTitles != null) {
            displayView(0);
        }
        super.onResume();
    }

    /***
     * Called when invalidateOptionsMenu() is triggered
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if nav drawer is opened, hide the action items
        boolean drawerOpen = drawerLayout.isDrawerOpen(slideMenu);
        menu.findItem(R.id.action_logout).setVisible(!drawerOpen);
        if (!drawerOpen) {
            FAB.setVisibility(View.VISIBLE);
        }
        else {
            FAB.setVisibility(View.GONE);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void setTitle(CharSequence title) {
        appTitle = title;
        getSupportActionBar().setTitle(appTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        int id = item.getItemId();
        if (id == R.id.action_logout) {
            try {
                EvernoteSession mEvernoteSession = EvernoteSessionConstant.getSession(MainActivity.this);
                mEvernoteSession.logOut(MainActivity.this);
                finish();
            }
            catch (InvalidAuthenticationException e) {
                Log.e(LOGTAG, e.getMessage());
            }
            return true;
        }
        else if (id == R.id.action_add_notebook) {
            createNotebook();
        }
        else if (id == R.id.action_sync) {
            pd.show();

            showSyncMessageFlag = true;
            syncNotebooksAndNotes();
            Cursor cursor = mService.getUnsyncedNotebooks();
            int nameIndex = cursor.getColumnIndexOrThrow(Notebooks.NAME);
            int idIndex = cursor.getColumnIndexOrThrow(Notebooks._ID);
            while (cursor.moveToNext()) {
                String notebookTitle = cursor.getString(nameIndex);
                long notebookId = cursor.getLong(idIndex);
                evernoteServiceHelper.saveNotebook(notebookTitle, notebookId);
            }

            cursor = mService.getUnsyncedNotes();
            idIndex = cursor.getColumnIndexOrThrow(Notes._ID);

            while (cursor.moveToNext()) {

                long noteId = cursor.getLong(idIndex);
                Note note = mService.getNote(noteId);
                // TODO костыль из-за кривой базы, т.к. getNotebookGuid() возвращает обычный id, что печально
                String notebookGuid = mService.getNotebook(Long.parseLong(note.getNotebookGuid())).getGuid();
                ParcelableNote parcelableNote = new ParcelableNote(note);
                parcelableNote.setNoteId(noteId);
                parcelableNote.setNotebookGuid(notebookGuid);
                evernoteServiceHelper.saveNote(parcelableNote);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            DBService.DBWriteBinder binder = (DBService.DBWriteBinder)iBinder;
            mService = binder.getClientApiService();
            MainActivity.this.inflateSidebar();
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

    private void inflateSidebar() {
        Cursor cursor = mService.getAllNotebooks();
        int nameIndex = cursor.getColumnIndexOrThrow(Notebooks.NAME);
        int idIndex = cursor.getColumnIndexOrThrow(Notebooks._ID);

        String[] slideMenuTitles = getResources().getStringArray(R.array.slide_menu_items);
        TypedArray slideMenuIcons = getResources().obtainTypedArray(R.array.slide_menu_icons);
        this.slideMenuTitles = new ArrayList<>();

        this.slideMenuItems = new ArrayList<>();
        for (int I = 0; I < slideMenuTitles.length; I++) {
            this.slideMenuTitles.add(slideMenuTitles[I]);
            this.slideMenuItems.add(new SlideMenuItem(slideMenuTitles[I], slideMenuIcons.getResourceId(I, -1)));

            // add all notebooks to slidebar
            if (I == 1) {
                while (cursor.moveToNext()) {
                    String notebookTitle = cursor.getString(nameIndex);
                    long notebookId = cursor.getLong(idIndex);
                    this.slideMenuItems.add(new NotebookMenuItem(notebookTitle, R.drawable.ic_drawer_white_notebooks, notebookId));
                    this.slideMenuTitles.add(notebookTitle);
                }
            }
        }

        SlideMenuAdapter adapter = new SlideMenuAdapter(MainActivity.this, slideMenuItems);
        slideMenu.setAdapter(adapter);
        slideMenu.setOnItemClickListener(new SlideMenuClickListener());

        displayView(0);
    }

    private void createNotebook() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final EditText edittext= new EditText(MainActivity.this);
        builder.setMessage("Новый блокнот");
        builder.setTitle("Введите название");

        builder.setView(edittext);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String notebookName = edittext.getText().toString();

                try {
                    long notebookId = mService.insertNotebook(notebookName);
                    inflateSidebar();
                    Toast.makeText(getApplicationContext(), R.string.notebook_created, Toast.LENGTH_LONG).show();
                    saveNotebookRequestId = evernoteServiceHelper.saveNotebook(notebookName, notebookId);
                }
                catch (SQLException e) {
                    Log.e(LOGTAG, e.getMessage());
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    private void syncNotebooksAndNotes() {
        notebooksRequestId = evernoteServiceHelper.getNotebooks();
        notesRequestId = evernoteServiceHelper.getAllNotes(100);
    }

    private void registerBroadcastReceiver() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long resultRequestId = intent.getLongExtra(EvernoteServiceHelper.EXTRA_REQUEST_ID, -1);
                int resultCode = intent.getIntExtra(EvernoteServiceHelper.EXTRA_RESULT_CODE, 0);

                if (showSyncMessageFlag && resultRequestId == notebooksRequestId) {
                    inflateSidebar();
                    showToast(resultCode, R.string.sync_notebooks_ok, R.string.sync_notebooks_error);
                }
                else if (showSyncMessageFlag && resultRequestId == notesRequestId) {
                    pd.dismiss();
                    displayView(0);
                    showToast(resultCode, R.string.sync_notes_ok, R.string.sync_notes_error);
                }
                else if (resultRequestId == saveNotebookRequestId) {
                    showToast(resultCode, R.string.sync_notebook_created, R.string.sync_error_notebook_created);
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
