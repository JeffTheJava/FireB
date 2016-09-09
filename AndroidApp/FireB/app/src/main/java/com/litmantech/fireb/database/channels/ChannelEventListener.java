package com.litmantech.fireb.database.channels;

/**
 * Created by Jeff_Dev_PC on 9/8/2016.
 */
public interface ChannelEventListener {
    void onChannelDataChanged();

    void onChannelCancelled(String message);
}
