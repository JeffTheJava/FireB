package com.litmantech.fireb.database.messages;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Jeff_Dev_PC on 9/9/2016.
 */
public class MessageRecyclerAdapter extends RecyclerView.Adapter<MessageRecyclerAdapter.MessageViewHolder> {


    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }





    class MessageViewHolder extends  RecyclerView.ViewHolder{

        public MessageViewHolder(View itemView) {
            super(itemView);
        }
    }
}
