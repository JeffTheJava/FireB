package com.litmantech.fireb.database;

import android.util.Log;

import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.litmantech.fireb.database.channels.Channel;
import com.litmantech.fireb.database.channels.ChannelEventListener;
import com.litmantech.fireb.database.channels.ChannelHandler;
import com.litmantech.fireb.database.messages.Message;
import com.litmantech.fireb.database.messages.MessageEventListener;
import com.litmantech.fireb.database.messages.MessagesHandler;

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
    private final MessagesHandler messagesHandler;

    /**
     * Do not instantiate  this object unless you have successfully logged in
     */
    public DatabaseHandler(){
        db = FirebaseDatabase.getInstance().getReference();
        channelHandler = new ChannelHandler();
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        messagesHandler = new MessagesHandler(db,mUser);
    }

    /**
     * this is safe to call as many times as you want. if we are already init then it will do nothing.
     * @param initListener
     */
    public void initChannels(DatabaseInitListener initListener){
        //check if we have already initialized if so the just return
        if(!channelHandler.getChannels().isEmpty()) return;
        if(channelHandler.isInitializing()) return;

        channelHandler.init(db,initListener);
    }



    public HashMap<String,Channel> getChannels() {
        return channelHandler.getChannels();
    }

    public void setChannelEventListener(ChannelEventListener channelEventListener) {
        channelHandler.setChannelEventListener(channelEventListener);
    }

    public void initMessages(Channel mChannel, DatabaseInitListener databaseInitListener) {
        messagesHandler.init(mChannel,databaseInitListener);

    }

    public void setMessageEventListener(MessageEventListener messageEventListener) {
        messagesHandler.setMessageEventListener(messageEventListener);
    }

    public ArrayList<Message> getMessages() {
        return messagesHandler.getMessages();
    }

    public void pushMessage(String message) {
        messagesHandler.pushMessageOldSchool(message,messagesHandler.getMessages().size());
        //messagesHandler.pushMessage(message);

    }

    public void newChannel(String channelName,String topic) {
        channelHandler.newChannel(channelName,topic);
    }

    public boolean isChannelInit() {
        return !channelHandler.getChannels().isEmpty();
    }

    public boolean isMessagesInit() {
        return messagesHandler.isInit();
    }

    public String getMessageTopic() {
        return messagesHandler.getMessageTopic();
    }
}
