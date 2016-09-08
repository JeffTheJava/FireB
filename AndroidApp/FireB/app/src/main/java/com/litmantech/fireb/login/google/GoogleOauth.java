package com.litmantech.fireb.login.google;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;
import com.litmantech.fireb.R;
import com.litmantech.fireb.login.LoginHandler;
import com.litmantech.fireb.login.OAuth;

/**
 * Created by Jeff_Dev_PC on 9/8/2016.
 */
public class GoogleOauth implements OAuth, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "GoogleOauth";
    private final GoogleApiClient mGoogleApiClient;
    private final Context mContext;

    public GoogleOauth(Context context){
        mContext = context;
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(context.getString(R.string.default_web_client_id)).requestEmail().build();
        mGoogleApiClient = new GoogleApiClient.Builder(context).enableAutoManage((FragmentActivity) context,this).addApi(Auth.GOOGLE_SIGN_IN_API,gso).build();

    }

    @Override
    public void signIn(Fragment activityForResult) {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        activityForResult.startActivityForResult(signInIntent, LoginHandler.SIGN_IN_CODE);
    }

    @Override
    public void signOut(ResultCallback<Status> resultCallback) {
        if(resultCallback == null) {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
        }else{
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(resultCallback);
        }
    }

    @Override
    public AuthCredential onSignInResult(Intent data) {
        GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
        if(result.isSuccess()){
            LogD("Google Sign Successfully");
            GoogleSignInAccount gAccount = result.getSignInAccount();
            return GoogleAuthProvider.getCredential(gAccount.getIdToken(), null);

        }else{
            LogD("Google Sign fail!!");
            return null;
        }
    }



    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void LogD(String message) {
        Log.d(TAG,message);
    }
}
