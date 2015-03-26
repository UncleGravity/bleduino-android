package com.kytelabs.bleduino.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kytelabs.bleduino.R;
import com.kytelabs.bleduino.pojos.ModuleListItem;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by aviera1 on 3/19/15.
 */
public class ModuleGridAdapter extends RecyclerView.Adapter<ModuleGridAdapter.ViewHolder> {

    private Context mContext;
    private List<ModuleListItem> mModules;

    public ModuleGridAdapter(Context context, List<ModuleListItem> modules) {
        mContext = context;
        mModules = modules;
    }

    @Override
    public ModuleGridAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.modules_list_item, parent, false);

        ModuleGridAdapter.ViewHolder vh = new ViewHolder(v, new ViewHolder.ViewHolderClick() {
            @Override
            public void moduleClick(View caller, int index) {

                Intent intent = new Intent(mContext, mModules.get(index).getNextClass());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
                Log.d("Test", "Index = " + index); // TODO Remove this log
            }
        });
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Get element from your dataset at this position
        // Replace the contents of the view with that element
        // Clear the ones that won't be used

        holder.moduleName.setText(mModules.get(position).getText());
        holder.imgViewIcon.setImageResource(mModules.get(position).getIconId());

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mModules.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements RecyclerView.OnClickListener {

        @InjectView(R.id.moduleName) TextView moduleName;
        @InjectView(R.id.imageView) ImageView imgViewIcon;
        public ViewHolderClick mListener;


        public ViewHolder(View itemLayoutView, ViewHolderClick listener) {
            super(itemLayoutView);
            mListener = listener;
            ButterKnife.inject(this, itemLayoutView);

            //itemView.setOnClickListener(this);
            imgViewIcon.setOnClickListener(this);
            moduleName.setOnClickListener(this);
            //itemLayoutView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.moduleClick(v, getPosition());
        }

        public static interface ViewHolderClick {
            public void moduleClick(View caller, int index);
        }

    }

}