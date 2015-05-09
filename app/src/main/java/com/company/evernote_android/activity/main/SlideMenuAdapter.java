package com.company.evernote_android.activity.main;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.company.evernote_android.R;

import java.util.ArrayList;

/**
 * Created by max on 09.04.15.
 */
public class SlideMenuAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<SlideMenuItem> menuItems;

    public SlideMenuAdapter(Context context, ArrayList<SlideMenuItem> menuItems){
        this.context = context;
        this.menuItems = menuItems;
    }

    @Override
    public int getCount() {
        return menuItems.size();
    }

    @Override
    public Object getItem(int position) {
        return menuItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.list_item, null);
        }

        TextView txtTitle = (TextView) convertView.findViewById(R.id.title);
        txtTitle.setText(menuItems.get(position).getTitle());

        return convertView;
    }

}