package com.company.evernote_android.activity.main;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.content.res.TypedArray;
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
import com.company.evernote_android.activity.ReadNoteActivity;
import com.company.evernote_android.provider.ClientAPI;
import com.company.evernote_android.provider.DBService;
import com.company.evernote_android.utils.EvernoteSessionConstant;

import com.company.evernote_android.activity.main.fragments.NotesFragment;

import com.company.evernote_android.sync.EvernoteServiceHelper;
import com.company.evernote_android.sync.rest.GetNotebooksRestMethod;

import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.InvalidAuthenticationException;


import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    private final static String LOGTAG = "MainActivity";

    public final static String NOTE_ID_KEY = "note_id";

    private DrawerLayout drawerLayout;
    private ListView slideMenu;
    private ActionBarDrawerToggle drawerToggle;

    // nav drawer title
    private CharSequence appTitle;
    private String[] slideMenuTitles;

    private EvernoteServiceHelper evernoteServiceHelper;
    private ClientAPI mService = null;

    ImageButton FAB;

    private class SlideMenuClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            displayView(position);
        }
    }

    private class NotesListClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(MainActivity.this, ReadNoteActivity.class);
            intent.putExtra(NOTE_ID_KEY, id);
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appTitle = getTitle();
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        slideMenu = (ListView) findViewById(R.id.slide_menu);

        SlideMenuAdapter adapter = new SlideMenuAdapter(MainActivity.this, loadSlideMenuItems());
        slideMenu.setAdapter(adapter);
        slideMenu.setOnItemClickListener(new SlideMenuClickListener());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.drawable.ic_app_logo);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));

        //start test
        evernoteServiceHelper = EvernoteServiceHelper.getInstance(this);
        evernoteServiceHelper.getNotebooks();



        // end test


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

        if (savedInstanceState == null) {
            displayView(0);
        }
    }

    private ArrayList<SlideMenuItem> loadSlideMenuItems() {
        slideMenuTitles = getResources().getStringArray(R.array.slide_menu_items);
        TypedArray slideMenuIcons = getResources().obtainTypedArray(R.array.slide_menu_icons);

        ArrayList<SlideMenuItem> slideMenuItems = new ArrayList<>();
        for (int I = 0; I < slideMenuTitles.length; I++)
            slideMenuItems.add(new SlideMenuItem(slideMenuTitles[I], slideMenuIcons.getResourceId(I, -1)));

        return slideMenuItems;
    }

    private void displayView(int position) {
        setTitle(slideMenuTitles[position]);
        drawerLayout.closeDrawer(slideMenu);

        // update the main content by replacing fragments
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new NotesFragment();
                break;
            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.list_frame, fragment).commit();
        }
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

        return super.onOptionsItemSelected(item);
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

    private void createNotebook() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final EditText edittext= new EditText(MainActivity.this);
        builder.setMessage("Новый блокнот");
        builder.setTitle("Введите название");

        builder.setView(edittext);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String notebookName = edittext.getText().toString();
                mService.insertNotebook(notebookName);
                Toast.makeText(getApplicationContext(), R.string.notebook_created, Toast.LENGTH_LONG).show();
            }
        });

        builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });

        builder.show();

    }
}
