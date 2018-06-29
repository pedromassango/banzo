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
}