package com.shid.clipboardmanagerkt.Service

import android.app.*
import android.content.ClipboardManager
import android.content.ClipboardManager.OnPrimaryClipChangedListener
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.Icon
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.shid.clipboardmanagerkt.Database.ClipDatabase
import com.shid.clipboardmanagerkt.Model.ClipEntry
import com.shid.clipboardmanagerkt.R
import com.shid.clipboardmanagerkt.UI.MainActivity
import com.shid.clipboardmanagerkt.Utils.Constant
import com.shid.clipboardmanagerkt.Utils.StopAutoListenReceiver
import kotlinx.coroutines.*
import java.util.*

class AutoListenService : Service() {

    companion object {
        private const val GENERAL_CHANNEL = "general channel"
        private var isServiceRunning = false
    }

    private lateinit var notificationManager: NotificationManager
    private lateinit var vNotification: Notification
    private var vNotification1: NotificationCompat.Builder? = null
    private lateinit var mClipboard: ClipboardManager
    private lateinit var listener: OnPrimaryClipChangedListener
    private var serviceJob = Job()


    // Member variable for the Database
    private lateinit var  mDb: ClipDatabase


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


    override fun onCreate() {
        super.onCreate()
        isServiceRunning = true
        mDb= ClipDatabase.getInstance(applicationContext)
        mClipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel()
        listener = OnPrimaryClipChangedListener {
            startPrimaryClipChangedListenerDelayThread()
            performClipboardCheck()
        }
    }

    private fun performClipboardCheck() {
        if (mClipboard.hasPrimaryClip()) {
            val copiedClip =
                mClipboard.primaryClip!!.getItemAt(0).text.toString()

            val date: Date = Date()
            val clipEntry = ClipEntry(entry = copiedClip, date = date)

            val uiScope = CoroutineScope(Dispatchers.Main + serviceJob)

            uiScope.launch {
                withContext(Dispatchers.IO) {
                    mDb!!.clipDao.insertClip(clipEntry)
                }
            }
            Toast.makeText(applicationContext, "Clip added", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroy() {
        serviceJob.cancel()
        isServiceRunning = false
        if (notificationManager != null) {
            notificationManager.cancel(Constant.NOTI_IDENTIFIER)
        }


        if (mClipboard != null && listener != null) {
            mClipboard.removePrimaryClipChangedListener(listener)
            Toast.makeText(
                applicationContext,
                " AutoListen Disabled, reset to use again",
                Toast.LENGTH_SHORT
            )
                .show()
        }
        super.onDestroy()
    }

    private fun startPrimaryClipChangedListenerDelayThread() {
        mClipboard.removePrimaryClipChangedListener(listener)
        val handler = Handler()
        handler.postDelayed({ mClipboard.addPrimaryClipChangedListener(listener) }, 1000)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                GENERAL_CHANNEL,
                resources.getString(R.string.general_channel),
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.setShowBadge(true)
            notificationChannel.lightColor = Color.MAGENTA
            notificationChannel.importance = NotificationManager.IMPORTANCE_HIGH
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}