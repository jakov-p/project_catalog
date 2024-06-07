package com.marlove.catalog.utilities

import android.content.Context
import android.net.ConnectivityManager

class InternetConnectionChecker(val context:Context) {
    val isConnected: Boolean
        get() {
            val connectivityManager =  context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = connectivityManager.activeNetworkInfo
            return netInfo != null && netInfo.isConnected
        }
}

