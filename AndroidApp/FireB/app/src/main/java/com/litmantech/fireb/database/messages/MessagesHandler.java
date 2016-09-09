package com.litmantech.fireb.database.messages;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.litmantech.fireb.database.DatabaseInitListener;
import com.litmantech.fireb.database.channels.Channel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Jeff_Dev_PC on 9/9/2016.
 */
public class MessagesHandler {
    private final DatabaseReference db;
    private final ArrayList<Message> messages = new ArrayList<>();
    private boolean initializing = false;
    private MessageEventListener messageEventListener;


    public MessagesHandler(DatabaseReference databaseReference){
        this.db =databaseReference;
    }
    public void init(Channel mChannel, final DatabaseInitListener initListener) {
        initializing = true;
        String mMessagesKey = "c:"+mChannel.getKey();
        DatabaseReference dbMessages = db.child(mMessagesKey+"/messages");
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

    private void BuildMessages(ArrayList messageHolder) {
        messages.clear();
        Iterator it = messageHolder.iterator();
        while (it.hasNext()) {
            updateMessage((HashMap) it.next());
        }
    }

    public void updateMessage(HashMap<String,Object> value) {
        long created = (long) value.get(Message.CREATED_KEY);
        String author = (String) value.get(Message.AUTHOR_KEY);
        String messageText = (String) value.get(Message.MESSAGE_KEY);

        Message message = new Message(author,created,messageText);
        messages.add(message);

    }

    public void setMessageEventListener(MessageEventListener messageEventListener) {
        this.messageEventListener = messageEventListener;
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }
}
