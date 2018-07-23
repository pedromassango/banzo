package com.pedromassango.banzo.services

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModelProviders
import com.pedromassango.banzo.data.WordsDatabase
import com.pedromassango.banzo.data.models.Word
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

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
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
                    learningWord.learning = userLearnTheWord(learningWord)
                    learningWord.learned = isLearnedWord( learningWord)

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

    /**
     * This function check if the user has learned that word
     * @param word the word to check
     * @return true if there is more hit than fail counter
     */
    private fun isLearnedWord(word: Word): Boolean{
        return word.hitCounter > word.failCount
    }

    /**
     * This function check if the user has played at least three time that word
     * @param word the word to check
     * @return true if there is at least one hit or fail counter.
     */
    private fun userLearnTheWord(word: Word): Boolean{
        return word.hitCounter > 3 || word.failCount > 3
    }
}