package com.litmantech.fireb.database.messages;

/**
 * Created by Jeff_Dev_PC on 9/9/2016.
 */
public interface MessageEventListener {
    void onMessageDataChanged();

    void onMessageCancelled(String message);
}
