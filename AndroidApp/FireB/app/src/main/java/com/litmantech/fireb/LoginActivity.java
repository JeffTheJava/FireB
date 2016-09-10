package com.litmantech.fireb;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.litmantech.fireb.database.DatabaseHandler;
import com.litmantech.fireb.login.LoginHandler;
import com.litmantech.fireb.login.SignInListener;

/**
 * Created by Jeff_Dev_PC on 9/9/2016.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener, SignInListener {

    private ImageView signInButton;
    private LoginHandler mLoginHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//don't let phone sleep

        this.signInButton = (ImageView)this.findViewById(R.id.login_button);
        signInButton.setOnClickListener(this);

        mLoginHandler = new LoginHandler(this);
        mLoginHandler.setSignInListener(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == LoginHandler.SIGN_IN_CODE){
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

    private void login() {
        signInButton.setImageResource(R.drawable.common_google_signin_btn_icon_light_disabled);
        signInButton.setEnabled(false);
        mLoginHandler.SignInGoogle(this);
    }
    @Override
    public void onSignInSuccessful() {
        Toast.makeText(this, "Authentication Worked.",Toast.LENGTH_SHORT).show();
        signInButton.setImageResource(R.drawable.login);
        signInButton.setEnabled(true);
        StartMainActivity();
    }

    private void StartMainActivity() {
        Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
        LoginActivity.this.startActivity(myIntent);
        this.finish();
    }

    @Override
    public void onSignInFail() {
        signInButton.setImageResource(R.drawable.login);
        signInButton.setEnabled(true);
        Toast.makeText(this, "Authentication failed.",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSignOut() {
        signInButton.setImageResource(R.drawable.login);
        signInButton.setEnabled(true);
    }
}
