package com.litmantech.fireb.database.messages;

/**
 * Created by Jeff_Dev_PC on 9/9/2016.
 */
public class Message {

    public static final String AUTHOR_KEY = "author";
    public static final String CREATED_KEY = "created";
    public static String MESSAGE_KEY = "message";
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
