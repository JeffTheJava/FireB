package com.litmantech.fireb;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.litmantech.fireb.database.DatabaseHandler;
import com.litmantech.fireb.gps.GPSData;

public class MainActivity extends AppCompatActivity {
    private DatabaseHandler dbHolder;
    private GPSData gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//don't let phone sleep
        dbHolder = new DatabaseHandler();
        //gps = new GPSData(this); TODO
        //gps.init(); TODO
    }

    public DatabaseHandler getDatabaseHandler(){
        return dbHolder;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //gps.onResume(); TODO
    }

    @Override
    protected void onPause() {
        super.onPause();
        //gps.onPause(); TODO
    }


}
