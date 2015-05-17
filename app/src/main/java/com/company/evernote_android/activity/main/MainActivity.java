package com.company.evernote_android.activity.main;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;

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

import com.company.evernote_android.utils.StatusCode;
import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.InvalidAuthenticationException;
import com.evernote.edam.type.Notebook;


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
    private ClientAPI mService = null;

    private BroadcastReceiver broadcastReceiver;

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

        syncNotebooksAndNotes();

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                long resulRequestId = intent.getLongExtra(EvernoteServiceHelper.EXTRA_REQUEST_ID, -1);

                if (notebooksRequestId == resulRequestId) {
                    String message;
                    if (intent.getIntExtra(EvernoteServiceHelper.EXTRA_RESULT_CODE, 0) == StatusCode.OK) {
                        message = "Notebook update success";
                    } else {
                        message = "Notebook update error";
                    }

                    Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
                    toast.show();
                }

                if (notesRequestId == resulRequestId) {
                    String message;
                    if (intent.getIntExtra(EvernoteServiceHelper.EXTRA_RESULT_CODE, 0) == StatusCode.OK) {
                       message = "Notes update success";
                    } else {
                        message = "Notes update error";
                    }

                    Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
                    toast.show();
                }

            }
        };

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
            IntentFilter filter = new IntentFilter(EvernoteServiceHelper.ACTION_REQUEST_RESULT);
            registerReceiver(broadcastReceiver, filter);
            syncNotebooksAndNotes();
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
                if (mService.insertNotebook(notebookName)) {
                    inflateSidebar();
                    Toast.makeText(getApplicationContext(), R.string.notebook_created, Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(getApplicationContext(), R.string.err_creating_notebook, Toast.LENGTH_LONG).show();
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

    private void syncNotebooksAndNotes() {
        evernoteServiceHelper = EvernoteServiceHelper.getInstance(this);
        notebooksRequestId = evernoteServiceHelper.getNotebooks();
        notesRequestId = evernoteServiceHelper.getAllNotes(100);
    }
}
