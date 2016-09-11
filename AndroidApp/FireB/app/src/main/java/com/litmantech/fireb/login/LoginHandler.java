package com.litmantech.fireb.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.litmantech.fireb.login.google.GoogleOauth;

/**
 * Created by Jeff_Dev_PC on 9/8/2016.
 */
public class LoginHandler {
    private static final String TAG = "LoginHandler";
    public static int SIGN_IN_CODE = 504;
    private final Context mContext;
    private FirebaseUser mFirebaseUser;
    private FirebaseAuth mFirebaseAuth;
    private OAuth oAuth;
    private SignInListener signInListener;
    private LoginState mState;

    public enum LoginState{
        SIGNED_IN, SIGNED_OUT, LOGGING_IN
    }

    public LoginHandler(Context context){
        mContext = context;
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mState = LoginState.SIGNED_OUT;
    }


    public boolean isLoggedIn() {
        return mFirebaseUser != null;
    }

    public FirebaseUser getUser() {
        return mFirebaseUser;
    }

    /**
     * Signing in with google will start a new activity. When that activity is done it will return its data to the
     * FragmentActivity you passed in.
     *
     * @param activityForResult
     */
    public void SignInGoogle(Activity activityForResult) {
        mState = LoginState.LOGGING_IN;
        if(oAuth == null) {
            oAuth = new GoogleOauth(mContext);
        }
        oAuth.signIn(activityForResult);
    }

    public void signOut() {

        if(oAuth!=null) {
            ResultCallback<Status> signOutCallBack = new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status) {
                    SignOutFireBase();
                }
            };
            oAuth.signOut(signOutCallBack);
        }else{
            SignOutFireBase();
        }

    }

    private void SignOutFireBase() {
        mFirebaseAuth.signOut();
        mFirebaseUser = null;
        signInListener.onSignOut();
    }

    /**
     * Pares the login request data to see if the user logged in successful.
     * Use this for account creating and re-logging in.
     * If you want to be notified if login was successful or not make sure to set the SignInListener
     * @see #setSignInListener(SignInListener)
     * @param data the data given back to your activity containing the login data.
     */
    public void onSignInResult(Intent data) {
        mState = LoginState.LOGGING_IN;
        AuthCredential credential = oAuth.onSignInResult(data);
        if(credential == null){
            mState = LoginState.SIGNED_OUT;
            signInListener.onSignInFail();
            return;
        }


        OnCompleteListener<AuthResult> onCompleteListener = new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                // If sign in fails, display a message to the user. If sign in succeeds
                // the auth state listener will be notified and logic to handle the
                // signed in user can be handled in the listener.
                if (!task.isSuccessful()) {
                    Log.w(TAG, "signInWithCredential", task.getException());
                    mFirebaseUser = null;
                    mState = LoginState.SIGNED_OUT;
                    signInListener.onSignInFail();
                } else {

                    mFirebaseUser = mFirebaseAuth.getCurrentUser();
                    mState = LoginState.SIGNED_IN;
                    signInListener.onSignInSuccessful();
                }

            }
        };
        mFirebaseAuth.signInWithCredential(credential).addOnCompleteListener((Activity) mContext,onCompleteListener);

    }


    /**
     * Set this if you want to be notified if login was successful or not.
     * @param signInListener will be triggered if login failed or successful. also if you
     *                       want to be notified when the user logs out.
     */
    public void setSignInListener(SignInListener signInListener) {
        this.signInListener = signInListener;
    }

    public LoginState getState() {
        return mState;
    }

    private void LogD(String message) {
        Log.d(TAG,message);
    }
}
