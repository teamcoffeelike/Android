package com.hanul.caramelhomecchiato.common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class CommonMethod {

    public static String ipConfig = "http://115.23.60.116:8080";

    // 네트워크에 연결되어 있는가
    public static boolean isNetworkConnected(Context context)
    {
        ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService( Context.CONNECTIVITY_SERVICE );

        return cm.getActiveNetworkInfo() != null;
    }

}
