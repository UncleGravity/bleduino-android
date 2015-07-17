package com.kytelabs.bleduino.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kytelabs.bleduino.R;
import com.kytelabs.bleduino.pojos.ConsoleListItem;

import java.util.List;

/**
 * Created by Angel Viera on 7/5/15.
 */
public class ConsoleListAdapter extends RecyclerView.Adapter<ConsoleListAdapter.ViewHolder> {

    //Member Variables
    //--------------------------------------------------------------------------------
    private Context mContext;
    private List<ConsoleListItem> mMessages;

    //================================================================================
    // Constructor
    //================================================================================
    public ConsoleListAdapter(Context context, List<ConsoleListItem> messages) {
        mContext = context;
        mMessages = messages;
    }


    //================================================================================
    // Overrides
    //================================================================================
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_console, parent, false);
        // set the view's size, margins, paddings and layout parameters
        //...

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String sourceName = mMessages.get(position).getMessageSourceName();
        String message = mMessages.get(position).getMessage();

        holder.mMessage.setText(message);
        holder.mSource.setText(sourceName + ":");
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    //================================================================================
    // ViewHolder Class
    //================================================================================
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mSource;
        public TextView mMessage;

        public ViewHolder(View v) {
            super(v);
            mMessage = (TextView) v.findViewById(R.id.consoleMessage);
            mSource = (TextView) v.findViewById(R.id.consoleSource);
        }
    }
}
