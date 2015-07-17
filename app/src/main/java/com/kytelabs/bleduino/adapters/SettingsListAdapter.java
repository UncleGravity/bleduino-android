package com.kytelabs.bleduino.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.kytelabs.bleduino.R;
import com.kytelabs.bleduino.pojos.SettingsListItem;

import java.util.List;

/**
 * Created by aviera1 on 3/26/15.
 */
public class SettingsListAdapter extends BaseAdapter {

    List<SettingsListItem> mItems;
    Context mContext;

    public SettingsListAdapter(Context context, List<SettingsListItem> items) {
        mItems = items;
        mContext = context;
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

            if (mItems.get(position).getItemType() == SettingsListItem.HEADER) {

                convertView = mInflater.inflate(R.layout.list_item_settings_header, null);
                TextView title = (TextView) convertView.findViewById(R.id.settingsHeaderText);
                SettingsListItem item = mItems.get(position);
                title.setText(item.getPrimaryText());
                convertView.setOnClickListener(null);

            }

            else if (mItems.get(position).getItemType() == SettingsListItem.DIVIDER) {
                convertView = mInflater.inflate(R.layout.list_item_settings_divider, null);
                convertView.setOnClickListener(null);

            }

            else if (mItems.get(position).getItemType() == SettingsListItem.TOGGLE) {

                convertView = mInflater.inflate(R.layout.list_item_settings_toggle, null);

                SettingsListItem item = mItems.get(position);
                TextView title = (TextView) convertView.findViewById(R.id.toggleText);
                title.setText(item.getPrimaryText());

                SwitchCompat toggleSwitch = (SwitchCompat) convertView.findViewById(R.id.settingsToggleSwitch);
                toggleSwitch.setChecked(item.isToggleState());

                toggleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        Log.d("TAG", "Toggle");
                        // TODO save changed state to user preferences
                    }
                });

            }

            else if (mItems.get(position).getItemType() == SettingsListItem.TWO_LINE) {

            }

            else if (mItems.get(position).getItemType() == SettingsListItem.TEXT_ICON) {
                convertView = mInflater.inflate(R.layout.list_item_settings_github, null);

                SettingsListItem item = mItems.get(position);
                TextView title = (TextView) convertView.findViewById(R.id.githubText);
                title.setText(item.getPrimaryText());

                ImageView icon = (ImageView) convertView.findViewById(R.id.githubItemIcon);
                icon.setImageResource(item.getIconId());
            }
        }

        return convertView;
    }

}
