package com.pedromassango.banzo.services

import android.app.Service
import android.content.Intent
import android.os.CountDownTimer
import android.os.IBinder
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import timber.log.Timber

/**
 * Created by Pedro Massango on 8/22/18.
 */
class TimerService : Service(){

    companion object {
        const val TIMER_ACTION = "TIMER_ACTION"
        const val KEY_TIME = "KEY_TIME"
    }

    private val INTERVAL = 1000L
    private val TIME = 120000L
    private lateinit var countDownTimer: CountDownTimer

    override fun onCreate() {
        super.onCreate()

        countDownTimer = object : CountDownTimer(TIME, INTERVAL){
            override fun onFinish() {
                Timber.i("onFinish()")

                val data = Intent(TIMER_ACTION)
                data.putExtra(KEY_TIME, 0)

                LocalBroadcastManager.getInstance(this@TimerService)
                        .sendBroadcast(data)
            }

            override fun onTick(p0: Long) {
                Timber.i("tick $p0")

                val data = Intent(TIMER_ACTION)
                data.putExtra(KEY_TIME, p0)

                LocalBroadcastManager.getInstance(this@TimerService)
                        .sendBroadcast(data)
            }
        }

        countDownTimer.start()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        // this will finish the timer
       // countDownTimer.cancel()
    }
}
