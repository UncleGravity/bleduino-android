package com.kytelabs.bleduino.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kytelabs.bleduino.R;
import com.kytelabs.bleduino.modules.LedModuleActivity;
import com.kytelabs.bleduino.pojos.LedListItem;

import java.util.List;

/**
 * Created by Angel Viera on 7/15/15.
 */
public class LedListAdapter extends RecyclerView.Adapter<LedListAdapter.ViewHolder>{

    //Member Variables
    //--------------------------------------------------------------------------------
    private Context mContext;
    private List<LedListItem> mLeds;
    OnLedClickListener mLedCallback; //Tell LedModuleActivity to do it's magic.

    //================================================================================
    // Constructor
    //================================================================================
    public LedListAdapter(Context context, List<LedListItem> leds) {
        mContext = context;
        mLeds = leds;

        try {
            mLedCallback = (OnLedClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(LedModuleActivity.class.toString()
                    + " must implement OnLedClickListener");
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_led_module, parent, false);
        // set the view's size, margins, paddings and layout parameters
        //...

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String pinName = mLeds.get(position).getPinName();

        holder.mPinName.setText("Pin: " + pinName);
        holder.mPinState.setChecked(mLeds.get(position).isPinState());
        holder.mLed = mLeds.get(position);
    }

    @Override
    public int getItemCount() {
        return mLeds.size();
    }


    //================================================================================
    // ViewHolder Class
    //================================================================================
    public class ViewHolder extends RecyclerView.ViewHolder implements RecyclerView.OnClickListener {
        // each data item is just a string in this case
        public TextView mPinName;
        public SwitchCompat mPinState;
        public LedListItem mLed;

        public ViewHolder(View v) {
            super(v);
            mPinName = (TextView) v.findViewById(R.id.ledToggleText);
            mPinState = (SwitchCompat) v.findViewById(R.id.ledToggleSwitch);
            mPinState.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            //mPinState.toggle();
            mLed.setPinState(mPinState.isChecked());
            mLedCallback.onLedClick(mLed);
        }
    }

    public interface OnLedClickListener{
        void onLedClick(LedListItem led);
    }

}
