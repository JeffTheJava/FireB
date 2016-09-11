/******************************************************************************
 Copyright (c) 2016, JeffmeJones & Litman Tech All rights reserved.
 Redistribution and use in source and binary forms, with or without modification,
 are permitted provided that the following conditions are met:

 Redistributions of source code must retain the above copyright notice, this list
 of conditions and the following disclaimer. Redistributions in binary form must
 reproduce the above copyright notice, this list of conditions and the following
 disclaimer in the documentation and/or other materials provided with the
 distribution. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND
 CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY,
 OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING
 IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 OF SUCH DAMAGE.
 ******************************************************************************/
package com.litmantech.fireb.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

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
 * This class is used to handle logging into firebase use google.
 * If will soon be updated to log into facebook or use your email and password.
 *
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

    /**
     * state used to help with UI.
     * Based off the the state of this handler you can choose how to update you buttons and text exc..
     */
    public enum LoginState{
        SIGNED_IN, SIGNED_OUT, LOGGING_IN
    }

    /**
     *
     * @param context
     */
    public LoginHandler(Context context){
        mContext = context;
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if(mFirebaseUser == null){
            mState = LoginState.SIGNED_OUT;
        }else{
            mState = LoginState.SIGNED_IN;
        }
    }

    /**
     *
     * @return
     */
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
        mFirebaseUser = null;
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
        mState = LoginState.SIGNED_OUT;
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
        mFirebaseUser = null;
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
