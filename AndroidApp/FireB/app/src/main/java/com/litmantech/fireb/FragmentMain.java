package com.litmantech.fireb;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.litmantech.fireb.login.LoginHandler;
import com.litmantech.fireb.login.SignInListener;

/**
 * Created by Jeff_Dev_PC on 9/6/2016.
 */
public class FragmentMain extends Fragment implements View.OnClickListener, SignInListener {

    private static final String TAG = FragmentMain.class.getSimpleName();
    private FirebaseUser mUser;

    private SignInButton signInButton;
    private Button signOutButton;
    private TextView dataTextView;
    private LoginHandler mLoginHandler;
    private TestChannelList testing;

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

        signInButton.setOnClickListener(this);
        signOutButton.setOnClickListener(this);

        testing = new TestChannelList(this.getActivity(),(RecyclerView) view.findViewById(R.id.recyclerView));
        updateUI();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mLoginHandler.isLoggedIn()){
            mUser = mLoginHandler.getUser();
            testing.onStart();
        }
        updateUI();

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
        updateUI();
    }

    @Override
    public void onSignInFail() {
        Toast.makeText(getActivity(), "Authentication failed.",Toast.LENGTH_SHORT).show();
        updateUI();
    }

    @Override
    public void onSignOut() {
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
    }
}
