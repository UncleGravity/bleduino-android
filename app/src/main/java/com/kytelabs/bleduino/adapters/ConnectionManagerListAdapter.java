package com.kytelabs.bleduino.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kytelabs.bleduino.R;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by aviera1 on 3/22/15.
 */
public class ConnectionManagerListAdapter extends RecyclerView.Adapter<ConnectionManagerListAdapter.ViewHolder> {

    private Context mContext;
    List<String> mCatNames; //Change to BLEduino object list

    public ConnectionManagerListAdapter(Context context) {
        mContext = context;
        randomizeCatNames();
    }

    public void randomizeCatNames() {
        mCatNames = Arrays.asList(getCatNamesResource());
        Collections.shuffle(mCatNames);
    }

    private String[] getCatNamesResource() {
        return mContext.getResources().getStringArray(R.array.cat_names);
    }

    public String getItem(int position) {
        return mCatNames.get(position);
    }

    @Override
    public ConnectionManagerListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(mContext).inflate(R.layout.list_item_ble, parent, false);
        return new ViewHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(ConnectionManagerListAdapter.ViewHolder holder, int position) {
        String catName = getItem(position);
        holder.mCatNameTextView.setText(catName);
    }

    @Override
    public int getItemCount() {
        return mCatNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mCatNameTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            mCatNameTextView = (TextView) itemView.findViewById(R.id.leText);
        }
    }
}
