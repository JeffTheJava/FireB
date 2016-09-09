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
    private OnChannelClickListener mOnChannelClickListener;
    private String mHighlightChannelKey = "";

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
        final Channel channel = (Channel) mChannels.values().toArray()[position];
        holder.setData(channel);
        holder.getItemView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChannelRecyclerAdapter.this.setHighlightedItemKey(channel.getKey());
                if (mOnChannelClickListener != null) {
                    mOnChannelClickListener.onChannelClick(channel);
                    notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mChannels.size();
    }

    public void setOnChannelClickListener(OnChannelClickListener onChannelClickListener){
        this.mOnChannelClickListener = onChannelClickListener;
    }

    public void cleanup() {
        mChannels.clear();
        clearHighlightChannelKey();
        this.notifyDataSetChanged();
    }

    private void clearHighlightChannelKey() {
        setHighlightedItemKey("");
    }

    public void setHighlightedItemKey(String highlightedItemKey) {
        this.mHighlightChannelKey = highlightedItemKey;
    }

    public interface OnChannelClickListener {
        void onChannelClick(Channel channel);
    }

    class ChannelViewHolder extends  RecyclerView.ViewHolder{
        private final TextView mTitle;
        private final TextView mDate;
        private final View mitemView;
        private String mKey = "";

        public ChannelViewHolder(View itemView){
            super(itemView);
            this.mitemView = itemView;
            this.mTitle = (TextView) itemView.findViewById(android.R.id.text1);
            this.mDate = (TextView) itemView.findViewById(android.R.id.text2);

        }

        public void setData(Channel data) {
            mTitle.setText(data.getTitle());
            //mDate.setText(getDate(data.getCreated(), "dd/MM/yyyy hh:mm:ss"));
            mDate.setText(mPrettyTime.format(new Date(data.getCreated()*1000))+" ... "+getDate(data.getCreated(), "hh:mm:ss dd/MM/yyyy"));
            mKey = data.getKey();
            highlightBackground();
        }

        public View getItemView() {
            return mitemView;
        }

        public String getDate(long milliSeconds, String dateFormat) {
            SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(milliSeconds*1000);//convert from unix time
            return formatter.format(calendar.getTime());
        }

        private void highlightBackground() {
            String currentHighlightedKey = ChannelRecyclerAdapter.this.mHighlightChannelKey;
            if(!currentHighlightedKey.isEmpty() && currentHighlightedKey.contentEquals(mKey)) {
                mitemView.setBackgroundColor(mitemView.getContext().getResources().getColor(android.support.v7.appcompat.R.color.highlighted_text_material_dark));
            }else{
                mitemView.setBackgroundColor(0);
            }
        }

    }
}
