package com.litmantech.fireb;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.SignInButton;
import com.litmantech.fireb.database.DatabaseHandler;
import com.litmantech.fireb.database.DatabaseInitListener;
import com.litmantech.fireb.database.channels.Channel;
import com.litmantech.fireb.database.messages.MessageEventListener;
import com.litmantech.fireb.database.messages.MessageRecyclerAdapter;
import com.litmantech.fireb.login.LoginHandler;

/**
 * Created by Jeff_Dev_PC on 9/8/2016.
 */
public class MessageFragment extends Fragment implements View.OnClickListener, MessageEventListener {
    public static final String TAG = "MessageFragment";
    private RecyclerView mRecyclerView;

    private Channel mChannel;
    private Button backButton;
    private DatabaseHandler dbHolder;
    private MessageRecyclerAdapter adapter;
    private EditText editText;
    private Button sendButton;


    public void setSelectedChannel(Channel channel) {
        this.mChannel = channel;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_two, container, false);

        TextView text2 = (TextView) view.findViewById(R.id.textView2);
        backButton = (Button) view.findViewById(R.id.back_button);
        sendButton = (Button) view.findViewById(R.id.send_button);
        editText = (EditText) view.findViewById(R.id.editText);

        backButton.setOnClickListener(this);
        sendButton.setOnClickListener(this);

        mRecyclerView = (RecyclerView)view.findViewById(R.id.recyclerView2);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));

        text2.setText("Channel:"+mChannel.getTitle());
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if ((keyEvent != null && (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_SEND)) {

                    sendEditTextMessage();
                }
                return false;
            }
        });

        dbHolder = ((MainActivity)this.getActivity()).getDatabaseHandler();
        dbHolder.setMessageEventListener(this);
        dbHolder.initMessages(mChannel, new DatabaseInitListener(){

            @Override
            public void onInitComplete() {
                adapter = new MessageRecyclerAdapter(MessageFragment.this.getActivity(),dbHolder.getMessages());
                mRecyclerView.setAdapter(adapter);

            }

            @Override
            public void onInitError(String message) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back_button:
                backPressed();
                break;
            case R.id.send_button:
                sendEditTextMessage();
        }
    }

    private void sendEditTextMessage() {
        String message = editText.getText().toString();
        if(message.isEmpty()) return;

        editText.setText("");

        InputMethodManager imm = (InputMethodManager)this.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);

        sendMessage(message);

    }

    private void sendMessage(String message){
        dbHolder.pushMessage(message);
    }

    private void backPressed() {
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }

    @Override
    public void onMessageDataChanged() {
        if(adapter.getItemCount() > 0) {
            mRecyclerView.scrollToPosition(adapter.getItemCount() - 1);
            mRecyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onMessageCancelled(String message) {

    }

    @Override
    public void onPause() {
        super.onPause();
        if(adapter!=null) {
            adapter.cleanup();
        }
    }
}
