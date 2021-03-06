package com.pedromassango.banzo.data.preferences

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.pedromassango.banzo.MainApplication
import com.pedromassango.banzo.enums.LanguagestTypes

/**
 * Created by Pedro Massango on 13/06/2017 at 21:52.
 */

class PreferencesHelper(context: Context = MainApplication.applicationContext()) {

    companion object {

        private const val KEY_FIRST_TIME = "KEY_FIRST_TIME"
        private const val KEY_LEARNING_DAY = "KEY_LEARNING_DAY"
        private const val KEY_TOTAL_WORDS_TO_LEARN = "KEY_TOTAL_WORDS_TO_LEARN"
        private const val KEY_LANG_TO_LEARN = "KEY_LANG_TO_LEARN"
        private const val KEY_USER_NAME = "KEY_USER_NAME"
        private const val KEY_USER_PHOTO_URL = "KEY_USER_PHOTO_URL"
        private const val KEY_ANIMATE_STATISTIC = "KEY_ANIMATE_STATISTIC"

    }

    private val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    private val editor = preferences.edit()

    var isFirstTime = preferences.getBoolean(KEY_FIRST_TIME, true)
        set(value) = editor.putBoolean(KEY_FIRST_TIME, value).apply()

    var lastLearnedDay = preferences.getInt(KEY_LEARNING_DAY, 0)
        set(value) = editor.putInt(KEY_LEARNING_DAY, value).apply()

    var totalWordsToLearn = preferences.getInt(KEY_TOTAL_WORDS_TO_LEARN, 0)
        set(value) = editor.putInt(KEY_TOTAL_WORDS_TO_LEARN, value).apply()

    var username = preferences.getString(KEY_USER_NAME, "")
        set(value) = editor.putString(KEY_USER_NAME, value).apply()

    var photoUrl = preferences.getString(KEY_USER_PHOTO_URL, "")
        set(value) = editor.putString(KEY_USER_PHOTO_URL, value).apply()

    private var languageToLearn = preferences.getInt(KEY_LANG_TO_LEARN, 0)
        set(value) = editor.putInt(KEY_LANG_TO_LEARN, value).apply()

    var animateStatistics = preferences.getBoolean(KEY_ANIMATE_STATISTIC, true)
        set(value) = editor.putBoolean(KEY_ANIMATE_STATISTIC, value).apply()

    fun setLangToLearn(type: LanguagestTypes){
        languageToLearn = type.value
    }

    fun getLangToLearn(): LanguagestTypes{
        return when(languageToLearn){
            0 -> LanguagestTypes.ENGLISH
            1 -> LanguagestTypes.ESPANHOL
            else -> LanguagestTypes.ENGLISH
        }
    }

}
