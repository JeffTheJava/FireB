package com.litmantech.fireb.database;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.litmantech.fireb.database.channels.Channel;
import com.litmantech.fireb.database.channels.ChannelEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Jeff_Dev_PC on 9/8/2016.
 */
public class DatabaseHandler {
    private final DatabaseReference db;
    private final HashMap<String,Channel> channels = new HashMap<>();
    private boolean isInitializing = false;
    private ChannelEventListener mChannelEventListener;

    /**
     * Do not instantiate  this object unless you have successfully logged in
     */
    public DatabaseHandler(){
         db = FirebaseDatabase.getInstance().getReference();
    }

    /**
     * this is safe to call as many times as you want. if we are already init then it will do nothing.
     * @param initListener
     */
    public void initChannels(final DatabaseInitListener initListener){
        //check if we have already initialized if so the just return
        if(!channels.isEmpty()) return;
        if(isInitializing) return;

        isInitializing = true;
        DatabaseReference dbChannels = db.child("channels");
        dbChannels.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String,Object> channelsHolder = (HashMap<String, Object>) dataSnapshot.getValue();
                BuildChannels(channelsHolder);
                if(isInitializing){//TODO i don't like this if statement i dont know how to fix it yet. will get back to it.
                    initListener.onInitComplete();
                    isInitializing = false;
                }

                if(mChannelEventListener !=null){
                    mChannelEventListener.onChannelDataChanged();
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                if(isInitializing){
                    isInitializing = false;
                    initListener.onInitError(databaseError.getMessage());
                }

                if(mChannelEventListener !=null){
                    mChannelEventListener.onChannelCancelled(databaseError.getMessage());
                }
            }
        });

    }

    private void BuildChannels(HashMap channelsHolder) {
        Iterator it = channelsHolder.entrySet().iterator();
        while (it.hasNext()) {
            HashMap.Entry pair = (HashMap.Entry)it.next();
            updateChannel((String) pair.getKey(), (HashMap) pair.getValue());
        }
    }

    /**
     * Will add entry if doesn't exists. Will update if exists.
     * @param key
     * @param value
     */
    public void updateChannel(String key,  HashMap<String,Object> value) {
        long created = (long) value.get(Channel.CREATED_KEY);
        String title = (String) value.get(Channel.TITLE_KEY);

        Channel channel = new Channel(key,created,title);
        channels.put(key,channel);
    }

    public HashMap<String,Channel> getChannels() {
        return channels;
    }

    public void setChannelEventListener(ChannelEventListener channelEventListener) {
        this.mChannelEventListener = channelEventListener;
    }
}
