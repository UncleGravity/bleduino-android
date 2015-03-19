package com.kytelabs.bleduino.pojos;

import android.support.v4.app.Fragment;

/**
 * Created by Angel Viera on 3/18/15.
 */
public class NavigationItem {

    private int mIconId;
    private String mText;
    private Class<? extends Fragment> mFragmentClass;
    private boolean isSelected;
    private boolean isHeader;
    private boolean isDivider;

    //================================================================================
    // Constructor
    //================================================================================
    /*
    public NavigationItem(int iconId, String text, Class<? extends Fragment> fragmentClass) {
        mIconId = iconId;
        mText = text;
        mFragmentClass = fragmentClass;
        isSelected = false;
    }
    */
    //================================================================================
    // Getters & Setters
    //================================================================================

    public int getIconId() {
        return mIconId;
    }

    public void setIconId(int iconId) {
        mIconId = iconId;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }

    public Class<? extends Fragment> getFragmentClass() {
        return mFragmentClass;
    }

    public void setFragmentClass(Class<? extends Fragment> fragmentClass) {
        mFragmentClass = fragmentClass;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public boolean isHeader() {
        return isHeader;
    }

    public void setHeader(boolean isHeader) {
        this.isHeader = isHeader;
    }

    public boolean isDivider() {
        return isDivider;
    }

    public void setDivider(boolean isDivider) {
        this.isDivider = isDivider;
    }
}
