package com.litmantech.fireb.database.messages;

import android.util.Log;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.litmantech.fireb.database.DatabaseInitListener;
import com.litmantech.fireb.database.channels.Channel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;

/**
 * Created by Jeff_Dev_PC on 9/9/2016.
 */
public class MessagesHandler {
    private static final String TAG = "MessagesHandler";
    private final DatabaseReference db;
    private final ArrayList<Message> messages = new ArrayList<>();
    private final FirebaseUser mUser;
    private boolean initializing = false;
    private MessageEventListener messageEventListener;
    private DatabaseReference dbMessagesRoot;
    private DatabaseReference dbMessages;
    private String messageTopic = "";


    public MessagesHandler(DatabaseReference databaseReference, FirebaseUser user) {
        this.db = databaseReference;
        this.mUser = user;
    }

    public void init(Channel mChannel, final DatabaseInitListener initListener) {
        initializing = true;
        String mMessagesKey = "c:"+mChannel.getKey();
        dbMessagesRoot = db.child(mMessagesKey);
        dbMessages = dbMessagesRoot.child("messages");
        setTopicListener();

        dbMessages.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList messageHolder = (ArrayList) dataSnapshot.getValue();
                BuildMessages(messageHolder);
               if(initializing){//TODO i don't like this if statement i dont know how to fix it yet. will get back to it.
                    initListener.onInitComplete();
                    initializing = false;
                }

                if(messageEventListener !=null){
                    messageEventListener.onMessageDataChanged();
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

               if(initializing){
                    initializing = false;
                    initListener.onInitError(databaseError.getMessage());
                }

                if(messageEventListener !=null){
                    messageEventListener.onMessageCancelled(databaseError.getMessage());
                }
            }
        });
    }

    private void setTopicListener() {
        dbMessagesRoot.child("topic").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                messageTopic = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void BuildMessages(ArrayList messageHolder) {
        messages.clear();
        if(messageHolder == null) return;
        Iterator it = messageHolder.iterator();
        while (it.hasNext()) {
            updateMessage((HashMap) it.next());
        }
    }

    public void updateMessage(HashMap<String,Object> value) {
        if(value==null) return;

        Object created =  value.get(Message.CREATED_KEY);
        if(created == null) return;

        Object author =  value.get(Message.AUTHOR_KEY);
        if(created == null) return;

        Object messageText = value.get(Message.MESSAGE_KEY);
        if(created == null) return;


        Message message = new Message((String) author,(long) created,(String) messageText);
        messages.add(message);

    }



    public void setMessageEventListener(MessageEventListener messageEventListener) {
        this.messageEventListener = messageEventListener;
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void pushMessage(String messageString) {

        long timeU = System.currentTimeMillis();
        Message message  = new Message(mUser.getDisplayName(),timeU,messageString);
        dbMessages.push().setValue(message);
    }

    public void pushMessageOldSchool(String messageString, int position) {
        long timeU = System.currentTimeMillis()/1000;
        Message message  = new Message(mUser.getDisplayName(),timeU,messageString);

        dbMessages.child(""+position).child(Message.AUTHOR_KEY).setValue(message.getAuthor());
        dbMessages.child(""+position).child(Message.CREATED_KEY).setValue(message.getCreated());
        dbMessages.child(""+position).child(Message.MESSAGE_KEY).setValue(message.getMessage());
    }

    private void LogE(String errorMessage) {
        Log.e(TAG,errorMessage);
    }

    private boolean checkIfNull(Object... nullObject) {
        return false;
    }

    public boolean isInit() {
        return messages.isEmpty();
    }

    public String getMessageTopic() {
        return messageTopic;
    }
}
