package com.pedromassango.banzo.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModelProviders
import com.pedromassango.banzo.data.WordsDatabase
import com.pedromassango.banzo.data.preferences.PreferencesHelper
import com.pedromassango.banzo.extras.DateUtils
import timber.log.Timber

/**
 * Created by Pedro Massango on 6/27/18.
 * This Receiver will be called if the device REBOOTED or DATE as changed.
 * We should check if it is a new day, if true get new words to learn,
 * else do nothing.
 */
class DateChangedReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        Timber.i("onReceive()")

        val wordsDatabase = WordsDatabase.getInstance(context!!).wordDAO

        // current day
        val currentDay = DateUtils.currentDay()
        val prefs = PreferencesHelper()

        // update last learned day if it is different of the current day
        if (currentDay != prefs.lastLearnedDay) {
            Timber.i("New day to learn...")
            // update lastLearnedDay
            prefs.lastLearnedDay = currentDay

            // Set previous words as learned words
            wordsDatabase.getLearningWordsList()?.let {
                Timber.i("removing ${it.size} from learning state...")

                // set as learned word
                it.forEach { learningWord ->
                    learningWord.learning = false
                    learningWord.learned = true

                    wordsDatabase.update(learningWord)
                }
                Timber.i("done")
            }

            // Generate new words to learn
            wordsDatabase.getWordsToLearnList()?.let {
                Timber.i("setting new words to learn...")

                // set new words in learning state
                it.forEach { wordToLearn ->
                    wordToLearn.learning = true

                    wordsDatabase.update(wordToLearn)
                }

                Timber.i("Done")
            }
        }else{
            Timber.i("Same day, keep user training.")
        }
    }
}