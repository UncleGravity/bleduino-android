package com.kytelabs.bleduino.pojos;

/**
 * Created by Angel Viera on 6/15/15.
 */
public class LeParsedDevice {

    public static final byte CONNECTED_LABEL = 0;
    public static final byte FOUND_LABEL = 1;

    private String mName;
    private String mAddress;
    private boolean isConnected;
    private boolean isConnectedLabel;
    private boolean isFoundLabel;

    public LeParsedDevice() {

    }

    public LeParsedDevice(int labelStatus) {
        if(labelStatus == CONNECTED_LABEL){
            isConnectedLabel = true;
            isFoundLabel = false;
            mName = "Connected";
        }

        else if(labelStatus == FOUND_LABEL){
            isConnectedLabel = false;
            isFoundLabel = true;
            mName = "Found";
        }
    }

    public LeParsedDevice(String name, String address, boolean connectionState) {
        mName = name;
        mAddress = address;
        isConnected = connectionState;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        mAddress = address;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    public boolean isConnectedLabel() {
        return isConnectedLabel;
    }

    public boolean isFoundLabel() {
        return isFoundLabel;
    }
}
