package com.company.evernote_android.activity;

import android.content.res.Configuration;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.company.evernote_android.R;
import com.company.evernote_android.adapter.SlideMenuAdapter;
import com.company.evernote_android.model.SlideMenuItem;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    private DrawerLayout drawerLayout;
    private ListView slideMenu;
    private ActionBarDrawerToggle drawerToggle;

    // nav drawer title
    private CharSequence slideMenuTitle;
    private CharSequence appTitle;
    private String[] slideMenuTitles;

    private ArrayList<SlideMenuItem> slideMenuItems;
    private SlideMenuAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        appTitle = slideMenuTitle = getTitle();

        // load slide menu items
        slideMenuTitles = getResources().getStringArray(R.array.slide_menu_items);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        slideMenu = (ListView) findViewById(R.id.slide_menu);

        slideMenuItems = new ArrayList<>();

        // adding nav drawer items to array
        slideMenuItems.add(new SlideMenuItem(slideMenuTitles[0]));
        slideMenuItems.add(new SlideMenuItem(slideMenuTitles[1]));
        slideMenuItems.add(new SlideMenuItem(slideMenuTitles[2]));
        slideMenuItems.add(new SlideMenuItem(slideMenuTitles[3]));

        // setting the nav drawer list adapter
        adapter = new SlideMenuAdapter(getApplicationContext(), slideMenuItems);
        slideMenu.setAdapter(adapter);
        slideMenu.setOnItemClickListener(new SlideMenuClickListener());

        // enabling action bar app icon and behaving it as toggle button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.string.app_name, // nav drawer open - description for accessibility
                R.string.app_name  // nav drawer close - description for accessibility
        ){
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(appTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(slideMenuTitle);
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);

        if (savedInstanceState == null) {
            // on first time display view for first nav item
            displayView(0);
        }
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
        menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
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
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
