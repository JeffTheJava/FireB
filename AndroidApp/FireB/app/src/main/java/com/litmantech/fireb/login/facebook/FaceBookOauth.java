package com.litmantech.fireb.login.facebook;

import android.content.Intent;
import android.support.v4.app.Fragment;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.AuthCredential;
import com.litmantech.fireb.login.OAuth;

/**
 * Created by Jeff_Dev_PC on 9/8/2016.
 */
public class FaceBookOauth implements OAuth {
    @Override
    public void signIn(Fragment activityForResult) {

    }

    @Override
    public void signOut(ResultCallback<Status> resultCallback) {

    }

    @Override
    public AuthCredential onSignInResult(Intent data) {
        return null;
    }
}
