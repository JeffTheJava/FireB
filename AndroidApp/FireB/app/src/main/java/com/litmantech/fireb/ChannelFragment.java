package com.litmantech.fireb;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.SignInButton;
import com.google.firebase.auth.FirebaseUser;
import com.litmantech.fireb.database.DatabaseHandler;
import com.litmantech.fireb.database.DatabaseInitListener;
import com.litmantech.fireb.database.channels.Channel;
import com.litmantech.fireb.database.channels.ChannelEventListener;
import com.litmantech.fireb.database.channels.ChannelRecyclerAdapter;
import com.litmantech.fireb.database.channels.NewChannelDialogFragment;
import com.litmantech.fireb.login.LoginHandler;
import com.litmantech.fireb.login.SignInListener;

/**
 * Created by Jeff_Dev_PC on 9/6/2016.
 */
public class ChannelFragment extends Fragment implements View.OnClickListener, SignInListener, ChannelEventListener, ChannelRecyclerAdapter.OnChannelClickListener , NewChannelDialogFragment.UserNameListener{

    private static final String TAG = ChannelFragment.class.getSimpleName();
    private FirebaseUser mUser;

    private RecyclerView mRecyclerView;
    private Button signOutButton;
    private TextView headerTextView;
    private LoginHandler mLoginHandler;
    private ChannelRecyclerAdapter adapter;
    private DatabaseHandler dbHolder;
    private Button newChannelButton;
    private ProgressDialog progress;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLoginHandler = new LoginHandler(this.getActivity());
        mLoginHandler.setSignInListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        signOutButton = (Button) view.findViewById(R.id.sign_out_button);
        newChannelButton = (Button) view.findViewById(R.id.new_channel);


        mRecyclerView = (RecyclerView)view.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));

        signOutButton.setOnClickListener(this);
        newChannelButton.setOnClickListener(this);

        updateUI();

        return view;
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        headerTextView = (TextView) this.getActivity().findViewById(R.id.header_text);
        updateUI();
    }

    @Override
    public void onResume() {
        super.onResume();
        dbHolder = ((MainActivity)this.getActivity()).getDatabaseHandler();

        if(mLoginHandler.getState() == LoginHandler.LoginState.SIGNED_IN){
            mUser = mLoginHandler.getUser();
            initChannelDataBase();
        }else{
            GoToLoginScreen();
        }
        updateUI();

    }

    private void GoToLoginScreen() {
        Intent myIntent = new Intent(this.getActivity(), LoginActivity.class);
        this.getActivity().startActivity(myIntent);
        this.getActivity().finish();
    }

    private void initChannelDataBase() {
        progress = new ProgressDialog(this.getActivity());
        progress.setMessage("loading...");
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
        progress.show();

        dbHolder.setChannelEventListener(this);
        dbHolder.initChannels(new DatabaseInitListener() {
            @Override
            public void onInitComplete() {
                adapter = new ChannelRecyclerAdapter(ChannelFragment.this.getActivity(),dbHolder.getChannels());
                adapter.setOnChannelClickListener(ChannelFragment.this);
                mRecyclerView.setAdapter(adapter);
                progress.dismiss();
                updateUI();
            }

            @Override
            public void onInitError(String message) {
                progress.dismiss();
                updateUI();
            }
        });
        if(dbHolder.isChannelInit()){
            progress.dismiss();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.sign_out_button:
                mLoginHandler.signOut();
                break;
            case R.id.new_channel:
                newChannel();
        }
    }

    private void newChannel() {
        FragmentManager manager = getFragmentManager();
        NewChannelDialogFragment editNameDialog = new NewChannelDialogFragment();
        editNameDialog.setUserNameListener(this);
        editNameDialog.show(manager, "fragment_edit_name");
    }



    @Override
    public void onSignInSuccessful() {
        Toast.makeText(getActivity(), "Authentication Worked.",Toast.LENGTH_SHORT).show();
        initChannelDataBase();
        updateUI();
    }

    @Override
    public void onSignInFail() {
        Toast.makeText(getActivity(), "Authentication failed.",Toast.LENGTH_SHORT).show();
        updateUI();
        GoToLoginScreen();

    }

    @Override
    public void onSignOut() {
        if(adapter != null){
            adapter.cleanup();
        }
        Toast.makeText(getActivity(), "SignOut.",Toast.LENGTH_SHORT).show();
        updateUI();
        GoToLoginScreen();
    }

    private void updateUI() {
        mUser = mLoginHandler.getUser();
        if(mLoginHandler.getState() != LoginHandler.LoginState.SIGNED_IN){
            signOutButton.setEnabled(false);
            setHeaderText("Please sign in!!");
        }else{
            signOutButton.setEnabled(true);
            setHeaderText(mUser.getDisplayName());
        }

        if(adapter != null){
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * the header view is part of the MainActivity not this fragment
     * the fragment might be ready to draw but not the activity
     * we need to check if it's null before setting it's value
     * @param message
     */
    private void setHeaderText(String message) {
        if(headerTextView != null) {
            headerTextView.setText(message);
        }
    }


    @Override
    public void onChannelDataChanged() {
        if(progress!=null){
            progress.dismiss();
        }
        updateUI();
    }

    @Override
    public void onChannelCancelled(String message) {
        updateUI();
    }

    @Override
    public void onChannelClick(Channel channel) {
        Log.e("testing",channel.getTitle());

        FragmentTransaction ft = this.getActivity().getSupportFragmentManager().beginTransaction();
        MessageFragment messageFragment = new MessageFragment();
        messageFragment.setSelectedChannel(channel);
        ft.addToBackStack(MessageFragment.TAG);//allow the back button to pop the fragment stack
        ft.replace(R.id.fragment_layout, messageFragment,MessageFragment.TAG);
        ft.commit();
    }

    @Override
    public void onFinishUserDialog(String channelName, String channelTopic) {
        dbHolder.newChannel(channelName,channelTopic);
    }
}
