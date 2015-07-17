package com.kytelabs.bleduino.pojos;

import android.app.Activity;

/**
 * Created by Angel Viera on 3/19/15.
 */
public class ModuleListItem {
    private int mIconId;
    private String mText;
    private Class<? extends Activity> nextClass;

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

    public Class<? extends Activity> getNextClass() {
        return nextClass;
    }

    public void setNextClass(Class<? extends Activity> nextClass) {
        this.nextClass = nextClass;
    }
}
