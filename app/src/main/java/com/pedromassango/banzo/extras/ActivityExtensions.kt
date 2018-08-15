package com.pedromassango.banzo.extras

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity

val start = { context: Context?,
              activity: Class<out AppCompatActivity> ->

    context?.startActivity( Intent(context, activity))
}
