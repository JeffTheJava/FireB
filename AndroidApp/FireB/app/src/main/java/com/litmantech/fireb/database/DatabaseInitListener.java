package com.litmantech.fireb.database;

/**
 * Created by Jeff_Dev_PC on 9/8/2016.
 */
public interface DatabaseInitListener {
    void onInitComplete();

    void onInitError(String message);
}
