package com.example.counterclicker

import android.app.Service
import android.content.Intent
import android.os.IBinder

class ClearService() : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)

        val prefs = Prefs(applicationContext)

        prefs.saveCounterValue(0)
    }
}