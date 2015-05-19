package com.company.evernote_android.activity.main;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.company.evernote_android.R;
import com.company.evernote_android.provider.EvernoteContract;

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
            convertView = mInflater.inflate(R.layout.slide_menu_item, null);
        }

        ImageView iconView = (ImageView) convertView.findViewById(R.id.icon);
        TextView titleView = (TextView) convertView.findViewById(R.id.title);
        TextView counterView = (TextView) convertView.findViewById(R.id.counter);

        SlideMenuItem item = menuItems.get(position);

        iconView.setImageResource(item.icon);
        titleView.setText(item.title);
        counterView.setText("");

        if (EvernoteContract.Notebooks.CONTENT_TYPE.equals(item.type())) {
            convertView.setPadding(40, 0, 0, 0);
        }

        return convertView;
    }

}