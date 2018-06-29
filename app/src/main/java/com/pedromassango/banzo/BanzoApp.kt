package com.pedromassango.banzo

import android.app.Application
import android.content.Context
import timber.log.Timber

/**
 * Created by Pedro Massango on 6/22/18.
 */
class BanzoApp : Application() {

    companion object {

        private var instance: BanzoApp? = null

        fun applicationContext(): Context {
            checkNotNull(instance)
            return instance!!.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this


        // Setup Timber logging library
        when (BuildConfig.DEBUG) {
            true -> Timber.plant(Timber.DebugTree())
            false -> Timber.plant(NoLogTree())
        }
    }

    inner class NoLogTree : Timber.Tree() {
        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {

        }
    }

}