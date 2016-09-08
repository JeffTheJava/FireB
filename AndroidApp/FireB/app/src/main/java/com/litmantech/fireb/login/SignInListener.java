package com.litmantech.fireb.login;

/**
 * Created by Jeff_Dev_PC on 9/8/2016.
 */
public interface SignInListener {
    void onSignInSuccessful();

    void onSignInFail();

    void onSignOut();
}
