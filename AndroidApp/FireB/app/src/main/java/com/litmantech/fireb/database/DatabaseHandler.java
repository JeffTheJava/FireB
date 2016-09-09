package com.litmantech.fireb.database;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.litmantech.fireb.database.channels.Channel;
import com.litmantech.fireb.database.channels.ChannelEventListener;
import com.litmantech.fireb.database.channels.ChannelHandler;

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
    private final ChannelHandler channelHandler;
    private boolean isInitializing = false;

    /**
     * Do not instantiate  this object unless you have successfully logged in
     */
    public DatabaseHandler(){
        db = FirebaseDatabase.getInstance().getReference();
        channelHandler = new ChannelHandler();
    }

    /**
     * this is safe to call as many times as you want. if we are already init then it will do nothing.
     * @param initListener
     */
    public void initChannels(DatabaseInitListener initListener){
        //check if we have already initialized if so the just return
        if(!channelHandler.getChannels().isEmpty()) return;
        if(isInitializing) return;

        channelHandler.init(db,initListener);
    }



    public HashMap<String,Channel> getChannels() {
        return channelHandler.getChannels();
    }

    public void setChannelEventListener(ChannelEventListener channelEventListener) {
        channelHandler.setChannelEventListener(channelEventListener);
    }

    public void initMessages(Channel mChannel) {

    }
}
