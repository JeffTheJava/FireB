package com.litmantech.fireb.database.messages;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.litmantech.fireb.database.channels.Channel;

import java.util.ArrayList;

/**
 * Created by Jeff_Dev_PC on 9/9/2016.
 */
public class MessageRecyclerAdapter extends RecyclerView.Adapter<MessageRecyclerAdapter.MessageViewHolder> {


    private final ArrayList<Message> mMessages;
    private final Context mContext;
    private final LayoutInflater mInflater;

    public MessageRecyclerAdapter(Context context, ArrayList<Message> messages) {
        this.mMessages = messages;
        this.mInflater = LayoutInflater.from(context);
        this.mContext = context;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(android.R.layout.two_line_list_item, parent,false);
        MessageViewHolder holder = new MessageViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        final Message message = mMessages.get(position);
        holder.setData(message);

    }


    public void cleanup() {
        mMessages.clear();
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }





    class MessageViewHolder extends  RecyclerView.ViewHolder{

        private final View mItemView;
        private final TextView mAuthor;
        private final TextView mMessage;

        public MessageViewHolder(View itemView) {
            super(itemView);
            this.mItemView = itemView;
            this.mAuthor = (TextView) itemView.findViewById(android.R.id.text1);
            this.mMessage = (TextView) itemView.findViewById(android.R.id.text2);
        }

        public void setData(Message data) {
            mAuthor.setText(data.getAuthor());
            mMessage.setText(data.getMessage());
        }
    }
}
