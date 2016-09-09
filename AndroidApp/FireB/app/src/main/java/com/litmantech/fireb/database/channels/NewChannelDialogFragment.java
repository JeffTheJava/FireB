package com.litmantech.fireb.database.channels;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.litmantech.fireb.R;

/**
 * Created by Jeff_Dev_PC on 9/9/2016.
 */
public class NewChannelDialogFragment extends DialogFragment implements TextView.OnEditorActionListener, View.OnClickListener {

    private UserNameListener mUserNameListener;
    private EditText mChannelName;
    private EditText mTopicName;
    private Button mCreateButton;

    @Override
    public void onClick(View view) {
        create();
    }


    public interface UserNameListener {
        void onFinishUserDialog(String channelName, String topicName);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_channel, container);
        mChannelName = (EditText) view.findViewById(R.id.edit_channel_name);
        mTopicName = (EditText) view.findViewById(R.id.edit_channel_topic);
        mCreateButton = (Button) view.findViewById(R.id.create_button);
        mCreateButton.setOnClickListener(this);

        mChannelName.setHint("Enter your Channel Name");
        mTopicName.setHint("Enter your Channel Topic");

        mTopicName.setOnEditorActionListener(this);
        mChannelName.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        getDialog().setTitle("Please enter Channel Info");

        return view;
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        create();
        return true;
    }

    private void create() {
        mUserNameListener.onFinishUserDialog(mChannelName.getText().toString(),mTopicName.getText().toString());
        this.dismiss();
    }

    public void setUserNameListener(UserNameListener userNameListener) {
        this.mUserNameListener = userNameListener;
    }
}