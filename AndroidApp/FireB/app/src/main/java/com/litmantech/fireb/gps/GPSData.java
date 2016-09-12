package com.litmantech.fireb.gps;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;

/**
 * Created by Jeff_Dev_PC on 9/12/2016.
 */
public class GPSData {
    private static final String LOCATION_SERVICE = "LOCATION_SERVICE";
    private final Context mContext;

    public GPSData(Context context){
        this.mContext = context;
    }

    public void init(){
        LocationManager service = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        boolean enabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);

        // check if enabled and if not send user to the GSP settings
        // Better solution would be to display a dialog and suggesting to
        // go to the settings
        if (!enabled) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            mContext.startActivity(intent);
        }
    }
}
