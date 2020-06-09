package com.shid.clipboardmanagerkt.Utils

import android.content.Context
import android.content.SharedPreferences

class SharedPref(context: Context) {

    private var sharedPref:SharedPreferences = context.getSharedPreferences("filename",Context.MODE_PRIVATE)

    fun setSwitch(state:Boolean){
        var editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putBoolean("Switch_state", state)
        editor.apply()
    }

    fun loadSwitchState() : Boolean{
        var state:Boolean = sharedPref.getBoolean("Switch_state",false)
        return state
    }

    fun setButtonState(state:Boolean){
        var editor = sharedPref.edit()
        editor.putBoolean("Button_state",state)
        editor.apply()
    }

    fun loadButtonState():Boolean{
        var state:Boolean = sharedPref.getBoolean("Button_state",false)
        return state
    }

    fun setNightMode(state:Boolean){
        var editor = sharedPref.edit()
        editor.putBoolean("NightMode",state)
        editor.apply()
    }

    fun loadNightMode():Boolean{
        var state = sharedPref.getBoolean("NightMode",false)
        return state
    }

}