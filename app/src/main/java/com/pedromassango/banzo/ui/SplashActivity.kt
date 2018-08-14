package com.pedromassango.banzo.ui

import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.pedromassango.banzo.data.preferences.PreferencesHelper
import com.pedromassango.banzo.extras.ActivityUtils

class SplashActivity : AppCompatActivity() {

    companion object{
        // start another activity delay time
        //private const val START_ACTIVITY_DELAY = 2000L
        private const val START_ACTIVITY_DELAY = 50L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // to check if it is first time
        val isFirstTime = PreferencesHelper().isFirstTime

        Handler().postDelayed({

            // Start MainActivity or SetupActivity depending on first time state
            when(isFirstTime){
                true -> ActivityUtils.start(this, SetupActivity::class.java)
                //true -> ActivityUtils.start(this, LearningActivity::class.java)
                false -> ActivityUtils.start(this, MainActivity::class.java)
            }.also {
                // finish activity when navigate to another activity
                this@SplashActivity.finish()
            }

        }, START_ACTIVITY_DELAY)
    }
}