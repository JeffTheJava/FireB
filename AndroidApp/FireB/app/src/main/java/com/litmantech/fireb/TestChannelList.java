package com.litmantech.fireb;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;


import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A class to play around with the channel list. nothing really crazy
 * Created by Jeff_Dev_PC on 9/8/2016.
 */
public class TestChannelList {
    private final RecyclerView mRecyclerView;
    private final DatabaseReference ref;
    private FirebaseRecyclerAdapter<String, MessageViewHolder> mFirebaseAdapter;

    public TestChannelList(Context context, RecyclerView recyclerView){
         ref = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://impekable-jeff.firebaseio.com/channels/general/title");
        mRecyclerView = recyclerView;
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
    }

    public void onStart(){


     mFirebaseAdapter = new FirebaseRecyclerAdapter<String, MessageViewHolder>
                (String.class, android.R.layout.two_line_list_item, MessageViewHolder.class,
                        ref) {

            @Override
            protected void populateViewHolder(MessageViewHolder viewHolder,
                                              String s, int position) {
                viewHolder.mTextView.setText(s);
            }
        };
        mRecyclerView.setAdapter(mFirebaseAdapter);
        mFirebaseAdapter.notifyDataSetChanged();

    }


    public static class MessageViewHolder extends RecyclerView.ViewHolder{
        TextView mTextView;
        public MessageViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(android.R.id.text1);
        }
    }
}
