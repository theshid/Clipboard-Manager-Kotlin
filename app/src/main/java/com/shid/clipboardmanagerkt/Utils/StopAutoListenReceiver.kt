package com.shid.clipboardmanagerkt.Utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.shid.clipboardmanagerkt.Service.AutoListenService

class StopAutoListenReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        context!!.applicationContext.stopService(Intent(context, AutoListenService::class.java))
        val sharedPref = SharedPref(context.applicationContext)
        sharedPref.setSwitch(false)
    }
}