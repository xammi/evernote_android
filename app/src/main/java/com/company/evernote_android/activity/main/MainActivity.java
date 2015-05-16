package com.company.evernote_android.activity.main;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import com.company.evernote_android.R;
import com.company.evernote_android.activity.NewNoteActivity;
import com.company.evernote_android.activity.SessionHolder;
import com.evernote.client.android.InvalidAuthenticationException;

import java.util.ArrayList;


public class MainActivity extends SessionHolder {

    private final static String LOGTAG = "MainActivity";

    private DrawerLayout drawerLayout;
    private ListView slideMenu;
    private ActionBarDrawerToggle drawerToggle;

    // nav drawer title
    private CharSequence appTitle;
    private String[] slideMenuTitles;

    ImageButton FAB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appTitle = getTitle();
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        slideMenu = (ListView) findViewById(R.id.slide_menu);

        SlideMenuAdapter adapter = new SlideMenuAdapter(getApplicationContext(), loadSlideMenuItems());
        slideMenu.setAdapter(adapter);
        slideMenu.setOnItemClickListener(new SlideMenuClickListener());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.drawable.ic_app_logo);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));

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

    public void displayView(int position) {
        setTitle(slideMenuTitles[position]);
        drawerLayout.closeDrawer(slideMenu);
    }

    /***
     * Called when invalidateOptionsMenu() is triggered
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if nav drawer is opened, hide the action items
        boolean drawerOpen = drawerLayout.isDrawerOpen(slideMenu);
        menu.findItem(R.id.action_logout).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void setTitle(CharSequence title) {
        appTitle = title;
        getSupportActionBar().setTitle(appTitle);
    }

    private class SlideMenuClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            displayView(position);
        }
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
                mEvernoteSession.logOut(MainActivity.this);
            }
            catch (InvalidAuthenticationException e) {
                Log.e(LOGTAG, e.getMessage());
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
