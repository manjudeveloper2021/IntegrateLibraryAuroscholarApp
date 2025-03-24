package com.auro.application

import android.annotation.SuppressLint
import android.app.Application
import android.os.Build
import android.os.Looper
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.os.StrictMode.VmPolicy
import dagger.hilt.android.HiltAndroidApp


class App : Application() {

    @SuppressLint("ObsoleteSdkInt")
    fun disableANR(){
        if (Build.VERSION.SDK_INT > 9) {
            val policy =
                ThreadPolicy.Builder().permitAll().detectAll().build()
            StrictMode.setThreadPolicy(policy)
            StrictMode.setVmPolicy(
                VmPolicy.Builder().detectAll()
                    .penaltyLog().penaltyDeath().build()
            )
        }
    }
    fun isMainThread(): Boolean {
        return Looper.myLooper() == Looper.getMainLooper()
    }
}