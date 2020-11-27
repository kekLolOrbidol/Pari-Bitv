package com.example.data

import android.content.Context
import android.content.SharedPreferences

class Settings(val context: Context) {

    var pref : SharedPreferences? = null

    fun getSharedPref(name : String){
        pref = context.getSharedPreferences(name, Context.MODE_PRIVATE)
    }

    fun getStroke(name : String) : String?{
        return pref?.getString(name, null)
    }

    fun putStroke(name : String, value : String){
        pref?.edit()?.putString(name, value)?.apply()
    }
}
