package com.kytelabs.bleduino.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kytelabs.bleduino.R;
import com.kytelabs.bleduino.pojos.NavigationItem;

import java.util.List;

/**
 * Created by Angel Viera on 3/18/15.
 */
public class DrawerListAdapter extends BaseAdapter {

    List<NavigationItem> mItems;
    Context mContext;

    public DrawerListAdapter(Context context, List<NavigationItem> mItems) {
        this.mItems = mItems;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            if(mItems.get(position).isHeader()){
                convertView = mInflater.inflate(R.layout.list_header_navigation, null);
                TextView headerText = (TextView) convertView.findViewById(R.id.headerText);
                NavigationItem item = mItems.get(position);
                headerText.setText(item.getText());
                convertView.setOnClickListener(null);

            } else if(mItems.get(position).isDivider()){
                convertView = mInflater.inflate(R.layout.list_divider_navigation, null);
                convertView.setOnClickListener(null);
            } else {

                convertView = mInflater.inflate(R.layout.list_item_navigation, null);

                ImageView icon = (ImageView) convertView.findViewById(R.id.drawerItemIcon);
                TextView title = (TextView) convertView.findViewById(R.id.drawerItemText);

                NavigationItem item = mItems.get(position);
                icon.setImageResource(item.getIconId());
                title.setText(item.getText());

                if (mItems.get(position).isSelected()) {
                    final int newColor = mContext.getResources().getColor(R.color.accentColor);
                    icon.setColorFilter(newColor, PorterDuff.Mode.SRC_ATOP);
                    title.setTextColor(newColor);
                } else {
                    icon.setColorFilter(Color.parseColor("#de000000"), PorterDuff.Mode.SRC_ATOP);

                }
            }
        }

        return convertView;
    }
}