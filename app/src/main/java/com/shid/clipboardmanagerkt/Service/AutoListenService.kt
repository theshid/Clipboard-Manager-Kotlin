package com.shid.clipboardmanagerkt.Service

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.ClipboardManager
import android.content.ClipboardManager.OnPrimaryClipChangedListener
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.Icon
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.shid.clipboardmanagerkt.Database.ClipDatabase
import com.shid.clipboardmanagerkt.R
import com.shid.clipboardmanagerkt.UI.MainActivity
import com.shid.clipboardmanagerkt.Utils.Constant
import com.shid.clipboardmanagerkt.Utils.StopAutoListenReceiver

class AutoListenService : Service() {

    companion object{
        private const val GENERAL_CHANNEL = "general channel"
    }

    private lateinit var notificationManager: NotificationManager
    private lateinit var vNotification: Notification
    private var vNotification1: NotificationCompat.Builder? = null
    private lateinit var mClipboard: ClipboardManager
    private  lateinit var listener: OnPrimaryClipChangedListener

    // Member variable for the Database
    private val mDb: ClipDatabase? = null
    var isServiceRunning = false

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val openActivityIntent = Intent(applicationContext, MainActivity::class.java)
        openActivityIntent.putExtra("service_on", true)
        openActivityIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        openActivityIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        val openActivityPIntent = PendingIntent.getActivity(
            applicationContext, Constant.AUTO_REQUEST_CODE, openActivityIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        // FLAG_UPDATE_CURRENT = if the described PendingIntent already exists,
        // then keep it but its replace its extra data with what is in this new Intent.


        // FLAG_UPDATE_CURRENT = if the described PendingIntent already exists,
        // then keep it but its replace its extra data with what is in this new Intent.
        val stopAutoIntent = Intent(applicationContext, StopAutoListenReceiver::class.java)
        stopAutoIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        stopAutoIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        val stopAutoPIntent =
            PendingIntent.getBroadcast(applicationContext, Constant.REQUEST_CODE, stopAutoIntent, 0)

        val bitmap =
            BitmapFactory.decodeResource(applicationContext.resources, R.mipmap.ic_service)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val icon: Icon =
                Icon.createWithResource(applicationContext, R.drawable.ic_stop_black)
            val builder =
                Notification.Action.Builder(icon, "STOP", stopAutoPIntent)
            vNotification =
                Notification.Builder(applicationContext, GENERAL_CHANNEL)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(bitmap)
                    .setContentTitle("AutoListen now activated")
                    .setContentText("Press to disable Clipboard Manager")
                    .setContentIntent(openActivityPIntent)
                    .setAutoCancel(true)
                    .addAction(builder.build())
                    .build()
            vNotification.flags = Notification.FLAG_NO_CLEAR
            notificationManager.notify(Constant.NOTI_IDENTIFIER, vNotification)
            startForeground(Constant.NOTI_IDENTIFIER, vNotification)
        } else {
            val action: NotificationCompat.Action =
                NotificationCompat.Action.Builder(
                    R.drawable.ic_stop_black,
                    "STOP", stopAutoPIntent
                )
                    .build()
            val builder2 =
                NotificationCompat.Builder(
                    applicationContext,
                    GENERAL_CHANNEL
                )
            val mNotification = builder2
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(bitmap)
                .setContentTitle("AutoListen now activated")
                .setContentText("Press to disable Clipboard Manager")
                .setContentIntent(openActivityPIntent)
                .setAutoCancel(true)
                .addAction(action)
                .build()
            mNotification.flags = Notification.FLAG_NO_CLEAR
            notificationManager.notify(Constant.NOTI_IDENTIFIER, mNotification)
            startForeground(Constant.NOTI_IDENTIFIER, mNotification)
        }

        mClipboard.addPrimaryClipChangedListener(listener)
        return START_STICKY
    }
}