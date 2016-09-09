package com.litmantech.fireb.database.channels;

/**
 * Created by Jeff_Dev_PC on 9/8/2016.
 */
public class Channel {
    public static final String CREATED_KEY = "created";
    public static final String TITLE_KEY = "title";
    private final String mKey;
    private final long mCreated;
    private final String mTitle;

    public Channel(String key,long created, String title){
        mKey = key;
        mCreated = created;
        mTitle = title;
    }

    public String getmKey() {
        return mKey;
    }

    public long getCreated() {
        return mCreated;
    }

    public String getTitle() {
        return mTitle;
    }
}
