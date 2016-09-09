package com.litmantech.fireb.database.messages;

/**
 * Created by Jeff_Dev_PC on 9/9/2016.
 */
public class Message {

    private final String mAuthor;
    private final long mCreated;
    private final String mMessage;

    public Message(String author, long created, String message){
        this.mAuthor = author;
        this.mCreated = created;
        this.mMessage = message;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public long getCreated() {
        return mCreated;
    }

    public String getMessage() {
        return mMessage;
    }
}
