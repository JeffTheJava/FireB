package com.litmantech.fireb.database.channels;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.litmantech.fireb.database.DatabaseInitListener;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Jeff_Dev_PC on 9/9/2016.
 */
public class ChannelHandler {
    private boolean initializing = false;
    private ChannelEventListener mChannelEventListener;
    private final LinkedHashMap<String,Channel> channels = new LinkedHashMap<>();
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
                if(initializing){//TODO i don't like this if statement i don't know how to fix it yet. will get back to it.
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
        HashMap dataHolder = new HashMap();

        Iterator it = channelsHolder.entrySet().iterator();
        while (it.hasNext()) {
            HashMap.Entry pair = (HashMap.Entry)it.next();
            Channel channel = parseChannel((String) pair.getKey(), (HashMap) pair.getValue());//this will return null
            if(channel == null) continue;
            dataHolder.put(channel.getKey(), channel);
        }

        channels.clear();
        sortByValues(dataHolder, channels);
    }


    /**
     * Be careful, Will return null
     * @param key
     * @param value
     * @return will return null
     */
    private Channel parseChannel(String key,HashMap<String, Object> value) {
        Object created =  value.get(Channel.CREATED_KEY);
        if(created == null) return null;

        Object title = value.get(Channel.TITLE_KEY);
        if(title == null) return null;

        Channel channel = new Channel(key,(long)created,(String)title);
        return channel;
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

    private void sortByValues(HashMap data, LinkedHashMap sortedHashMap) {
        List list = new LinkedList(data.entrySet());
        // Defined Custom Comparator here
        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Comparable) ((Channel)((Map.Entry) (o2)).getValue()).getCreated()).compareTo(((Channel)((Map.Entry) (o1)).getValue()).getCreated());
            }
        });

        // Here I am copying the sorted list in HashMap
        // using LinkedHashMap to preserve the insertion order
        for (Iterator it = list.iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            sortedHashMap.put(entry.getKey(), entry.getValue());
        }
    }
}
