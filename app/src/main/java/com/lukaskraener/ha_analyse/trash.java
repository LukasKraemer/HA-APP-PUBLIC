package com.lukaskraener.ha_analyse;

import android.content.Context;
import android.net.ConnectivityManager;

import java.net.InetAddress;

public class trash {
    public static boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("https://google.com");
            //You can replace it with your name
            return !ipAddr.equals("");

        } catch (Exception e) {
            return false;
        }
    }
}
