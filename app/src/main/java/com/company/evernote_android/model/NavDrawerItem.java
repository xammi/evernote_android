package com.company.evernote_android.model;

/**
 * Created by max on 09.04.15.
 */
public class NavDrawerItem {
    private String title;

    public NavDrawerItem() {}

    public NavDrawerItem(String title) {
        this.title = title;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
