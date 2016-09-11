package com.litmantech.fireb;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.litmantech.fireb.login.LoginHandler;
import com.litmantech.fireb.login.SignInListener;

/**
 * Created by Jeff_Dev_PC on 9/9/2016.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener, SignInListener {

    private ImageView mSignInButton;
    private LoginHandler mLoginHandler;
    private TextView mLoginText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//don't let phone sleep

        mSignInButton = (ImageView)findViewById(R.id.login_button);
        mLoginText = (TextView)findViewById(R.id.login_text);

        mSignInButton.setOnClickListener(this);

        mLoginHandler = new LoginHandler(this);
        mLoginHandler.setSignInListener(this);//set this to be notified if login was successful or not

        updateUI();
        DisplayBottomAppInfo();
    }

    /**
     * When logging in android will start a new Activity and present it to the user (dialog box or whatever).
     * We then need a way for android to pass the data back to this Activity. That's what why we need onActivityResult.     *
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == LoginHandler.SIGN_IN_CODE){//make sure the data var contains our login data
            //parse the data var. setSignInListener will let you know if login was Successful or not
            mLoginHandler.onSignInResult(data);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.login_button:
                login();
                break;
        }
    }

    /**
     * Update the ui based off the current start of the login handler
     */
    private void updateUI(){
        LoginHandler.LoginState currentLoginState = mLoginHandler.getState();

        updateOnScreenText(currentLoginState);
        updateButtonState(currentLoginState);
    }

    /**
     * Show the user some text on screen to let then know the state of the login process
     * @param currentLoginState
     */
    private void updateOnScreenText(LoginHandler.LoginState currentLoginState) {
        String loginText = "";
        switch (currentLoginState){
            case LOGGING_IN:
                loginText = getString(R.string.login_loading);
                break;
            case SIGNED_IN:
                String userName = mLoginHandler.getUser().getDisplayName();
                loginText = getString(R.string.login_welcome) + " " +userName;
                break;
            case SIGNED_OUT:
                loginText = getString(R.string.login_boot_message);
                break;
        }

        mLoginText.setText(loginText);
    }

    /**
     * enable or disable the button based on the current state of the login handler
     * @param currentLoginState
     */
    private void updateButtonState(LoginHandler.LoginState currentLoginState) {
        switch (currentLoginState){
            case LOGGING_IN:
                DisableSignInButton();
                break;
            case SIGNED_IN:
                DisableSignInButton();
                break;
            case SIGNED_OUT:
                EnableSignInButton();
                break;
        }

    }

    private void login() {
        //you can choose the type of authentication you want to use
        //right now only SignIn with google works but soon you will be able to call sign with email or signin will facebook....
        mLoginHandler.SignInGoogle(this);
        updateUI();
    }

    /**
     * You don't have to check if the user is logged in or not.
     * at the start of LoginActivity it will check before it does anything.
     * It will kick you out and bring you back to this activity if the user is not logged in.
     */
    private void StartMainActivity() {
        Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
        LoginActivity.this.startActivity(myIntent);
        this.finish();
    }

    @Override
    public void onSignInSuccessful() {
        Toast.makeText(this, "Authentication Worked.",Toast.LENGTH_SHORT).show();
        updateUI();
        StartMainActivity();
    }

    @Override
    public void onSignInFail() {
        updateUI();
        Toast.makeText(this, "Authentication failed.",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSignOut() {
        mSignInButton.setImageResource(R.drawable.login);
        mSignInButton.setEnabled(true);
    }

    private void EnableSignInButton() {
        mSignInButton.setImageResource(R.drawable.login);
        mSignInButton.setEnabled(true);
    }

    private void DisableSignInButton() {
        mSignInButton.setImageResource(R.drawable.common_google_signin_btn_icon_light_disabled);
        mSignInButton.setEnabled(false);
    }

    /**
     * at the bottom of the log ing screen show details about the app
     */
    private void DisplayBottomAppInfo() {
        String version ="";
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            version = "V."+pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
           //not doing anything here. don't care if it fails
        }
        String bottomAppInfo = "Created by Jeff Jones\n"+version;
        ((TextView) findViewById(R.id.app_info)).setText(bottomAppInfo);
    }
}
