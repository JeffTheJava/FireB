package com.litmantech.fireb.database.channels;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jeff_Dev_PC on 9/8/2016.
 */
public class ChannelRecyclerAdapter extends RecyclerView.Adapter<ChannelRecyclerAdapter.ChannelViewHolder> {
    final HashMap<String,Channel> mChannels;
    private final LayoutInflater mInflater;
    private final PrettyTime mPrettyTime;

    public ChannelRecyclerAdapter(Context context, HashMap<String, Channel> channels){
        this.mChannels = channels;
        this.mInflater = LayoutInflater.from(context);
        mPrettyTime = new PrettyTime();
    }

    @Override
    public ChannelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(android.R.layout.two_line_list_item, parent,false);
        ChannelViewHolder holder = new ChannelViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ChannelViewHolder holder, int position) {
        Channel channel = (Channel) mChannels.values().toArray()[position];
        holder.setData(channel);
    }

    @Override
    public int getItemCount() {
        return mChannels.size();
    }

    public void cleanup() {
        mChannels.clear();
        this.notifyDataSetChanged();
    }


    class ChannelViewHolder extends  RecyclerView.ViewHolder{
        private final TextView mTitle;
        private final TextView mDate;

        public ChannelViewHolder(View itemView){
            super(itemView);
            this.mTitle = (TextView) itemView.findViewById(android.R.id.text1);
            this.mDate = (TextView) itemView.findViewById(android.R.id.text2);

        }

        public void setData(Channel data) {
            mTitle.setText(data.getTitle());
            //mDate.setText(getDate(data.getCreated(), "dd/MM/yyyy hh:mm:ss"));
            mDate.setText(mPrettyTime.format(new Date(data.getCreated()*1000))+" ... "+getDate(data.getCreated(), "hh:mm:ss dd/MM/yyyy"));

        }

        public String getDate(long milliSeconds, String dateFormat) {
            SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(milliSeconds*1000);//convert from unix time
            return formatter.format(calendar.getTime());
        }

    }
}
