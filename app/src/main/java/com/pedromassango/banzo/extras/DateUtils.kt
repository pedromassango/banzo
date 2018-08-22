package com.pedromassango.banzo.extras

import java.util.*

/**
 * Created by Pedro Massango on 6/27/18.
 */
object DateUtils {

    /**
     * Get the current day from calendar
     * @return the current day
     */
    fun currentDay(): Int{
        return Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
    }

    fun getMinuteAndSecond(mTime: Long): String {
        val second = mTime / 1000 % 60
        val minute = mTime / (1000 * 60) % 60

        return String.format("%02d:%02d", minute, second)
    }
}