package com.pedromassango.banzo.data.preferences

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.pedromassango.banzo.BanzoApp

/**
 * Created by Pedro Massango on 13/06/2017 at 21:52.
 */

class PreferencesHelper(context: Context = BanzoApp.applicationContext()) {

    companion object {

        private const val KEY_FIRST_TIME = "KEY_FIRST_TIME"
        private const val KEY_LEARNING_DAY = "KEY_LEARNING_DAY"
        private const val KEY_TOTAL_WORDS_TO_LEARN = "KEY_TOTAL_WORDS_TO_LEARN"

    }

    private val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    private val editor = preferences.edit()

    var isFirstTime = preferences.getBoolean(KEY_FIRST_TIME, true)
        set(value) = editor.putBoolean(KEY_FIRST_TIME, value).apply()

    var lastLearnedDay = preferences.getInt(KEY_LEARNING_DAY, 0)
        set(value) = editor.putInt(KEY_LEARNING_DAY, value).apply()

    var totalWordsToLearn = preferences.getInt(KEY_TOTAL_WORDS_TO_LEARN, 0)
        set(value) = editor.putInt(KEY_TOTAL_WORDS_TO_LEARN, value).apply()

}
