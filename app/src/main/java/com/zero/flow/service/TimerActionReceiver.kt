package com.zero.flow.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class TimerActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action
        if (action != null) {
            val broadcastIntent = Intent(TIMER_ACTION)
            broadcastIntent.putExtra(TIMER_ACTION, action)
            context.sendBroadcast(broadcastIntent)
        }
    }

    companion object {
        const val TIMER_ACTION = "com.zero.flow.TIMER_ACTION"
        const val ACTION_PAUSE = "com.zero.flow.ACTION_PAUSE"
        const val ACTION_STOP = "com.zero.flow.ACTION_STOP"
    }
}
