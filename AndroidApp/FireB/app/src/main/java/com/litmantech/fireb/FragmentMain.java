package com.litmantech.fireb;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
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

/**
 * Created by Jeff_Dev_PC on 9/6/2016.
 */
public class FragmentMain extends Fragment implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private static final String TAG = FragmentMain.class.getSimpleName();
    private static int SIGN_IN_CODE = 504;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    private SignInButton signInButton;
    private Button signOutButton;
    private TextView dataTextView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        signInButton = (SignInButton) view.findViewById(R.id.sign_in_button);
        signOutButton = (Button) view.findViewById(R.id.sign_out_button);
        dataTextView = (TextView) view.findViewById(R.id.textView);

        signInButton.setOnClickListener(this);
        signOutButton.setOnClickListener(this);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        initGoogleOauth();

        updateUI();
        return view;
    }

    private void updateUI() {

        if(mFirebaseUser == null){
            signInButton.setEnabled(true);
            signOutButton.setEnabled(false);
            dataTextView.setText("Please sign in!!");
        }else{
            signInButton.setEnabled(false);
            signOutButton.setEnabled(true);
            dataTextView.setText("Signed in user :"+mFirebaseUser.getDisplayName());
        }
    }

    private void initGoogleOauth() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        mGoogleApiClient = new GoogleApiClient.Builder(this.getActivity()).enableAutoManage(this.getActivity(),this).addApi(Auth.GOOGLE_SIGN_IN_API,gso).build();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.sign_in_button:
                signIn();
                break;
            case R.id.sign_out_button:
                signOut();
                break;
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent,SIGN_IN_CODE);
    }

    private void signOut(){
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                FragmentMain.this.onSignOut(status);
            }
        });
        mFirebaseAuth.signOut();
        mFirebaseUser = null;
        updateUI();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SIGN_IN_CODE){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {

        if(result.isSuccess()){
            LogD("Google Sign Successfully");
            onSignIn(result);
        }else{
            LogD("Google Sign fail!!");
            dataTextView.setText("Unable to sign in");
        }
    }

    private void onSignIn(GoogleSignInResult result) {
        GoogleSignInAccount gAccount = result.getSignInAccount();
        AuthCredential credential = GoogleAuthProvider.getCredential(gAccount.getIdToken(), null);




        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this.getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(FragmentMain.this.getActivity(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(FragmentMain.this.getActivity(), "Authentication Worked.",
                                    Toast.LENGTH_SHORT).show();
                            mFirebaseUser = mFirebaseAuth.getCurrentUser();
                        }
                        updateUI();
                    }
                });

    }

    private void onSignOut(Status status) {
    }


    private void LogD(String message) {
        Log.d(TAG,message);
    }
}
