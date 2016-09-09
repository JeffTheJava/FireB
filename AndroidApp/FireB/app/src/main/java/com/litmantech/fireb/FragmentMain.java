package com.litmantech.fireb;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

import com.firebase.client.Firebase;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.litmantech.fireb.database.DatabaseHandler;
import com.litmantech.fireb.database.DatabaseInitListener;
import com.litmantech.fireb.database.channels.Channel;
import com.litmantech.fireb.database.channels.ChannelEventListener;
import com.litmantech.fireb.database.channels.ChannelRecyclerAdapter;
import com.litmantech.fireb.login.LoginHandler;
import com.litmantech.fireb.login.SignInListener;

/**
 * Created by Jeff_Dev_PC on 9/6/2016.
 */
public class FragmentMain extends Fragment implements View.OnClickListener, SignInListener, ChannelEventListener, ChannelRecyclerAdapter.OnChannelClickListener {

    private static final String TAG = FragmentMain.class.getSimpleName();
    private FirebaseUser mUser;

    private RecyclerView mRecyclerView;
    private SignInButton signInButton;
    private Button signOutButton;
    private TextView dataTextView;
    private LoginHandler mLoginHandler;
    private ChannelRecyclerAdapter adapter;
    private DatabaseHandler dbHolder;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLoginHandler = new LoginHandler(this.getActivity());
        mLoginHandler.setSignInListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        signInButton = (SignInButton) view.findViewById(R.id.sign_in_button);
        signOutButton = (Button) view.findViewById(R.id.sign_out_button);
        dataTextView = (TextView) view.findViewById(R.id.textView);

        mRecyclerView = (RecyclerView)view.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));

        signInButton.setOnClickListener(this);
        signOutButton.setOnClickListener(this);

        updateUI();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        dbHolder = ((MainActivity)this.getActivity()).getDatabaseHandler();

        if(mLoginHandler.isLoggedIn()){
            mUser = mLoginHandler.getUser();
            initChannelDataBase();

        }
        updateUI();

    }

    private void initChannelDataBase() {
        dbHolder.setChannelEventListener(this);
        dbHolder.initChannels(new DatabaseInitListener() {
            @Override
            public void onInitComplete() {
                adapter = new ChannelRecyclerAdapter(FragmentMain.this.getActivity(),dbHolder.getChannels());
                adapter.setOnChannelClickListener(FragmentMain.this);
                mRecyclerView.setAdapter(adapter);
                updateUI();
            }

            @Override
            public void onInitError(String message) {
                updateUI();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.sign_in_button:
                mLoginHandler.SignInGoogle(this);
                break;
            case R.id.sign_out_button:
                mLoginHandler.signOut();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == LoginHandler.SIGN_IN_CODE){
            mLoginHandler.onSignInResult(data);
        }
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
    }

    @Override
    public void onSignOut() {
        if(adapter != null){
            adapter.cleanup();
        }
        Toast.makeText(getActivity(), "SignOut.",Toast.LENGTH_SHORT).show();
        updateUI();
    }

    private void updateUI() {
        mUser = mLoginHandler.getUser();
        if(!mLoginHandler.isLoggedIn()){
            signInButton.setEnabled(true);
            signOutButton.setEnabled(false);
            dataTextView.setText("Please sign in!!");
        }else{
            signInButton.setEnabled(false);
            signOutButton.setEnabled(true);
            dataTextView.setText("Signed in user :"+mUser.getDisplayName());
        }

        if(adapter != null){
            adapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onChannelDataChanged() {
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
        FragmentTwo fragmentTwo = new FragmentTwo();
        fragmentTwo.setSelectedChannel(channel);
        ft.addToBackStack(FragmentTwo.TAG);//allow the back button to pop the fragment stack
        ft.replace(R.id.fragment_layout, fragmentTwo,FragmentTwo.TAG);
        ft.commit();
    }
}
