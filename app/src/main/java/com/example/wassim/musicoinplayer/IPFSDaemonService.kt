package com.example.wassim.musicoinplayer


import android.app.IntentService
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.util.Log

class IPFSDaemonService : IntentService("IPFSDaemonService") {

    private var nManager: NotificationManager? = null
    private var daemon: Process? = null
    internal var NOTIFICATION_ID = 12345

    override fun onHandleIntent(intent: Intent) {
        val exitIntent = Intent(this, IPFSDaemonService::class.java)
        exitIntent.action = "STOP"
        val pendingExit = PendingIntent.getService(this, 0, exitIntent, 0)

        try {
            daemon = IPFSDaemon(baseContext).run("daemon")
            State.isDaemonRunning = true
            daemon!!.waitFor()
            Log.d("zboub", "IPFSDaemonService started");
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        daemon!!.destroy()
        super.onDestroy()
        State.isDaemonRunning = false
        nManager?.cancel(NOTIFICATION_ID)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val action = intent.action
        if (nManager != null && action != null && action == "STOP") {
            stopSelf()
        }
        return super.onStartCommand(intent, flags, startId)
    }

}
