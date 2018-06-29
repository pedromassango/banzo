package com.pedromassango.banzo.extras

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * Created by Pedro Massango on 2/16/18.
 */
object ActivityUtils {

    val start = { context: Context?,
                  activity: Class<out AppCompatActivity> ->

        context?.startActivity( Intent(context, activity))
    }

    val startWith = { context: Context?,
                      data: Bundle,
                      activity: Class<out AppCompatActivity> ->

        val intent = Intent(context, activity).apply {
            putExtras(data)
        }

        context?.startActivity( intent)
    }

}