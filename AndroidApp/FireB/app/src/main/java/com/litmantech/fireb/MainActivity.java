package com.litmantech.fireb;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

import com.litmantech.fireb.database.DatabaseHandler;

public class MainActivity extends AppCompatActivity {
    private DatabaseHandler dbHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//don't let phone sleep
        dbHolder = new DatabaseHandler();
    }

    public DatabaseHandler getDatabaseHandler(){
        return dbHolder;
    }
}
