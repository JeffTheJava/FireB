package com.litmantech.fireb.database.messages;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.litmantech.fireb.database.channels.Channel;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Jeff_Dev_PC on 9/9/2016.
 */
public class MessageRecyclerAdapter extends RecyclerView.Adapter<MessageRecyclerAdapter.MessageViewHolder> {


    private final ArrayList<Message> mMessages;
    private final Context mContext;
    private final LayoutInflater mInflater;
    private final PrettyTime mPrettyTime;

    public MessageRecyclerAdapter(Context context, ArrayList<Message> messages) {
        this.mMessages = messages;
        this.mInflater = LayoutInflater.from(context);
        this.mContext = context;
        mPrettyTime = new PrettyTime();
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
            String mMessageString = data.getMessage();
            long timeholder = convertTimeIfNeeded(data.getCreated());

            String mTime = mPrettyTime.format(new Date(timeholder))+" ... "+getDate(timeholder, "hh:mm:ss dd/MM/yyyy");
            mMessage.setText(mMessageString+"\n"+mTime+"\n\n");
        }



        public String getDate(long milliSeconds, String dateFormat) {
            SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(milliSeconds);
            return formatter.format(calendar.getTime());
        }

        //convert from unix time
        private long convertTimeIfNeeded(long time) {
            int length = String.valueOf(time).length();

            if(length<= 10){
                return time*1000;
            }else{
                return time;
            }
        }
    }
}
