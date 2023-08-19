package com.beta.ssky10.aram

import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDexApplication
import com.beta.ssky10.aram.models.MySharedPreferences

class App : MultiDexApplication() {
    companion object{
        lateinit var prefs : MySharedPreferences
    }

    override fun onCreate() {
        prefs = MySharedPreferences(applicationContext)
        super.onCreate()
        Log.e("App:ableNightMode", prefs.ableNightMode.toString())
        Log.e("App:isAutoNightMode", prefs.isFixedNightMode.toString())
        if(prefs.ableNightMode){
            if(prefs.isFixedNightMode) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}