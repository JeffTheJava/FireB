package com.litmantech.fireb;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * A class to play around with the channel list. nothing really crazy
 * Created by Jeff_Dev_PC on 9/8/2016.
 */
public class TestChannelList {
    private final RecyclerView mRecyclerView;
    private DatabaseReference ref;
    private FirebaseRecyclerAdapter<Chat, ChatHolder> mAdapter;

    public TestChannelList(Context context, RecyclerView recyclerView){
        mRecyclerView = recyclerView;
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
    }

    public void onStart(){


        DatabaseReference refHolder = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://impekable-jeff.firebaseio.com/c:design");
        ref = refHolder.child("messages");
        DatabaseReference refTopic = refHolder.child("topic");
        refTopic.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String topic = dataSnapshot.getValue(String.class);
                Log.e("TESTING",topic);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //Chat msg = new Chat("puf", "1234", "Hello2 FirebaseUI world!");
        //ref.push().setValue(msg);

        mAdapter = new FirebaseRecyclerAdapter<Chat, ChatHolder>(Chat.class, android.R.layout.two_line_list_item, ChatHolder.class, ref) {
            @Override
            public void populateViewHolder(ChatHolder chatMessageViewHolder, Chat chatMessage, int position) {
                chatMessageViewHolder.setAuthor(chatMessage.getAuthor());
                chatMessageViewHolder.setMessage(chatMessage.getMessage());
            }
        };
        mRecyclerView.setAdapter(mAdapter);

    }


    public static class ChatHolder extends RecyclerView.ViewHolder {
        View mView;

        public ChatHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setAuthor(String author) {
            TextView field = (TextView) mView.findViewById(android.R.id.text1);
            field.setText(author);
        }

        public void setMessage(String message) {
            TextView field = (TextView) mView.findViewById(android.R.id.text2);
            field.setText(message);
        }
    }


    public static class Chat {

        String author;
        String message;
        long created;

        public Chat() {
        }

        public Chat(String author, long created, String message) {
            this.author = author;
            this.message = message;
            this.created = created;
        }

        public String getAuthor() {
            return author;
        }

        public long getUid() {
            return created;
        }

        public String getMessage() {
            return message;
        }
    }
}
