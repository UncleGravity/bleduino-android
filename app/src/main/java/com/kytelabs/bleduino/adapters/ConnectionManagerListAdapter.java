package com.kytelabs.bleduino.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.kytelabs.bleduino.R;
import com.kytelabs.bleduino.fragments.ConnectionManagerFragment;
import com.kytelabs.bleduino.pojos.LeParsedDevice;

import java.util.List;

/**
 * Created by aviera1 on 3/22/15.
 */
public class ConnectionManagerListAdapter extends RecyclerView.Adapter<ConnectionManagerListAdapter.ViewHolder> {

    //Member Variables
    //--------------------------------------------------------------------------------
    private Context mContext;
    List<LeParsedDevice> mLeDevices; //Change to BLEduino object list
    OnDeviceSelectedListener mDeviceCallback; //Tell ConnectionManagerFragment to do it's magic.

    //================================================================================
    // Constructor
    //================================================================================
    public ConnectionManagerListAdapter(Context context, ConnectionManagerFragment connectionManagerFragment, List<LeParsedDevice> leDevices) {
        mContext = context;
        mLeDevices = leDevices;

        try {
            mDeviceCallback = connectionManagerFragment;
        } catch (ClassCastException e) {
            throw new ClassCastException(connectionManagerFragment.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    //================================================================================
    // Overrides
    //================================================================================
    @Override
    public ConnectionManagerListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(mContext).inflate(R.layout.list_item_ble, parent, false);
        return new ViewHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(ConnectionManagerListAdapter.ViewHolder holder, int position) {

        String deviceName = mLeDevices.get(position).getName();

        // Handle devices with no device name available.
        if(deviceName == null || deviceName.equals("")){
            deviceName = "Unknown";
        }
        holder.mLeNameTextView.setText(deviceName);
        holder.mLeParsedDevice = mLeDevices.get(position);

        // proof of concept for text styling (section dividers)
        // TODO Style based on "Connected" or "Disconnected".
        if(mLeDevices.get(position).isConnectedLabel() || mLeDevices.get(position).isFoundLabel()){
            holder.mLeNameTextView.setPadding(dpToPx(16), dpToPx(8), 0, 0);
            holder.mLeNameTextView.setTextColor(Color.parseColor("#8a000000"));
            holder.itemView.setOnClickListener(null);
            holder.itemView.setEnabled(false);
        } else{
            Animation animation = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_in_left);
            holder.itemView.startAnimation(animation);
        }

        /*if (mDevices.get(position).isConnectedTitle() || mDevices.get(position).isFoundTitle()) {
            holder.mLeNameTextView.setPadding(35, 0, 0, 0);
            holder.mLeNameTextView.setTextColor("Title Color");
        }*/

    }

    public static int dpToPx(int dp)
    {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    @Override
    public int getItemCount() {
        return mLeDevices.size();
    }


    //================================================================================
    // ViewHolder Class
    //================================================================================
    public class ViewHolder extends RecyclerView.ViewHolder implements RecyclerView.OnClickListener {
        TextView mLeNameTextView;
        LeParsedDevice mLeParsedDevice;

        public ViewHolder(View itemView) {
            super(itemView);
            mLeNameTextView = (TextView) itemView.findViewById(R.id.leText);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.d("ConnectionAdapter","Click");
            Log.d("Device Name: ", mLeParsedDevice.getName());
            Log.d("Device Address: ", mLeParsedDevice.getAddress());

            if(!mLeParsedDevice.isFoundLabel() && !mLeParsedDevice.isConnectedLabel()) {
                mDeviceCallback.onDeviceSelected(mLeParsedDevice);
            }
        }
    }

    public interface OnDeviceSelectedListener {
        public void onDeviceSelected(LeParsedDevice clickedDevice);
    }

}
