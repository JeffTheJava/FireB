package com.litmantech.fireb;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.litmantech.fireb.database.DatabaseHandler;
import com.litmantech.fireb.gps.GPSData;

public class MainActivity extends AppCompatActivity {
    private DatabaseHandler dbHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//don't let phone sleep
        dbHolder = new DatabaseHandler();
        GPSData gps = new GPSData(this);
        gps.init();
    }

    public DatabaseHandler getDatabaseHandler(){
        return dbHolder;
    }
}
