package com.kytelabs.bleduino.pojos;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Angel Viera on 7/13/15.
 */
public class LedListItem {
    private String mPinName;
    private boolean mPinState;

    Map<String, Integer> mPinMap = new HashMap<String, Integer>();

    public LedListItem(String pinName) {
        mPinName = pinName;

        mPinMap.put("0",0);
        mPinMap.put("1",1);
        mPinMap.put("2",2);

        mPinMap.put("3",3);
        mPinMap.put("4",4);
        mPinMap.put("5",5);

        mPinMap.put("6",6);
        mPinMap.put("7",7);
        mPinMap.put("8",8);

        mPinMap.put("9",9);
        mPinMap.put("10",10);
        mPinMap.put("13",13);

        mPinMap.put("A0",18);
        mPinMap.put("A1",19);
        mPinMap.put("A2",20);

        mPinMap.put("A3",21);
        mPinMap.put("A4",22);
        mPinMap.put("A5",23);

        mPinMap.put("MOSI",16);
        mPinMap.put("MISO",14);
        mPinMap.put("SCK",15);
    }

    public String getPinName() {
        return mPinName;
    }

    public void setPinName(String pinName) {
        mPinName = pinName;
    }

    public boolean isPinState() {
        return mPinState;
    }

    public void setPinState(boolean pinState) {
        mPinState = pinState;
    }

    public void togglePinState() { mPinState = !mPinState; }

    public int getPinNumber(){
        return mPinMap.get(mPinName);
    }
}
