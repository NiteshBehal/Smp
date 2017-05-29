package com.simplified.text.android.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Singleton Class to check network state and to display error dialogue.
 */
public class CheckNetworkStatus {

    /**
     * Method to check if device is online or not.
     */
    private CheckNetworkStatus() {
        /**
         * Empty constructor
         */
    }
    /**
     * Method to check if device is online or not.
     *
     * @param mainContext Context
     */
    public static boolean isOnline(Context mainContext) {
        ConnectivityManager cm = (ConnectivityManager) mainContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if ((netInfo != null) && (netInfo.isConnected())) {
            return true;
        }
        return false;
    }
}
