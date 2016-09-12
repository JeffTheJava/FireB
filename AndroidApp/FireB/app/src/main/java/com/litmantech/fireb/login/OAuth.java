package com.litmantech.fireb.login;

import android.app.Activity;
import android.content.Intent;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.AuthCredential;

/**
 * Created by Jeff_Dev_PC on 9/8/2016.
 */
public interface OAuth {
    /**
     *
     * @param activityForResult The fragment you wish to have return the data
     */
    void signIn(Activity activityForResult);

    void signOut(ResultCallback<Status> resultCallback);

    /**
     * Will return Null if unable to signIn
     * @param data
     * @return Firebase Auth sign In Credential or Null
     */
    AuthCredential onSignInResult(Intent data);


}
