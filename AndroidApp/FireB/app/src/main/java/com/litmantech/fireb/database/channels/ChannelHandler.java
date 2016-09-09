package com.litmantech.fireb.database.channels;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.litmantech.fireb.database.DatabaseInitListener;
import com.litmantech.fireb.database.messages.Message;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Jeff_Dev_PC on 9/9/2016.
 */
public class ChannelHandler {
    private boolean initializing = false;
    private ChannelEventListener mChannelEventListener;
    private final HashMap<String,Channel> channels = new HashMap<>();
    private DatabaseReference dbChannels;
    private DatabaseReference dbRoot;


    public void init(DatabaseReference db, final DatabaseInitListener initListener) {
        initializing = true;
        dbRoot = db;
        dbChannels = db.child("channels");
        dbChannels.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                channels.clear();
                HashMap<String,Object> channelsHolder = (HashMap<String, Object>) dataSnapshot.getValue();
                BuildChannels(channelsHolder);
                if(initializing){//TODO i don't like this if statement i dont know how to fix it yet. will get back to it.
                    initListener.onInitComplete();
                    initializing = false;
                }

                if(mChannelEventListener !=null){
                    mChannelEventListener.onChannelDataChanged();
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                if(initializing){
                    initializing = false;
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
        Object created =  value.get(Channel.CREATED_KEY);
        if(created == null) return;

        Object title = value.get(Channel.TITLE_KEY);
        if(title == null) return;

        Channel channel = new Channel(key,(long)created,(String)title);
        channels.put(key,channel);
    }

    public void setChannelEventListener(ChannelEventListener channelEventListener) {
        this.mChannelEventListener = channelEventListener;
    }

    public HashMap<String, Channel> getChannels() {
        return channels;
    }

    public boolean isInitializing() {
        return initializing;
    }

    public void newChannel(String channelName, String topic) {
        if(channelName.isEmpty()) return;
        if(topic.isEmpty()) return;

        long timeU = System.currentTimeMillis()/1000;

        String channelKey = channelName.replaceAll(" ","_");

        dbChannels.child(channelKey).child(Channel.TITLE_KEY).setValue(channelName);
        dbChannels.child(channelKey).child(Channel.CREATED_KEY).setValue(timeU);

        dbRoot.child("c:"+channelKey).child(Channel.TOPIC_KEY).setValue(topic);

    }
}
