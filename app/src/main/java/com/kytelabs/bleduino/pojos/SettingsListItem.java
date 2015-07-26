package com.kytelabs.bleduino.pojos;

/**
 * Created by Angel Viera on 3/26/15.
 */
public class SettingsListItem {

    public static final int TOGGLE = 0;
    public static final int HEADER = 1;
    public static final int DIVIDER = 2;
    public static final int TWO_LINE = 3;
    public static final int TEXT_ICON = 4;

    public static String SETTINGS_FILE = "bleduino_settings";
    public static String SETTING_FILTER = "Only Scan BLEduinos";
    public static String SETTING_NOTIFY = "Notify on disconnect";
    public static String SETTING_REMINDER = "Connection reminder alert";

    private int mItemType;
    private String mPrimaryText;
    private String mSecondaryText; //app version (on the bottomm, grayed out)
    private int mIconId;
    private boolean mToggleState;

    public int getItemType() {
        return mItemType;
    }

    public void setItemType(int itemType) {
        mItemType = itemType;
    }

    public String getPrimaryText() {
        return mPrimaryText;
    }

    public void setPrimaryText(String primaryText) {
        mPrimaryText = primaryText;
    }

    public String getSecondaryText() {
        return mSecondaryText;
    }

    public void setSecondaryText(String secondaryText) {
        mSecondaryText = secondaryText;
    }

    public int getIconId() {
        return mIconId;
    }

    public void setIconId(int iconId) {
        mIconId = iconId;
    }

    public boolean isToggleState() {
        return mToggleState;
    }

    public void setToggleState(boolean toggleState) {
        mToggleState = toggleState;
    }
}
