package com.company.evernote_android.activity.main;

/**
 * Created by max on 09.04.15.
 */
public class SlideMenuItem {
    public String title;
    public int icon;
    private int counter;

    public SlideMenuItem(String title, int icon) {
        this.title = title;
        this.icon = icon;
        this.counter = 0;
    }

    public String getCounterValue() {
        return Integer.toString(counter);
    }

    public String type() {
        return null;
    }
}
