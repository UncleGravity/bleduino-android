package com.kytelabs.bleduino.pojos;

/**
 * Created by Angel Viera on 7/5/15.
 */
public class ConsoleListItem {
    private String mMessageSourceName;
    private String mMessage;
    //private Date mTimeStamp?


    public String getMessageSourceName() {
        return mMessageSourceName;
    }

    public void setMessageSourceName(String messageSourceName) {
        mMessageSourceName = messageSourceName;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        mMessage = message;
    }
}
