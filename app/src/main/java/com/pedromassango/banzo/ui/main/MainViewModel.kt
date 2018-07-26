package com.pedromassango.banzo.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.pedromassango.banzo.data.WordsDatabase


class MainViewModel : ViewModel() {
    // database
    private val wordsDatabase = WordsDatabase.getInstance().wordDAO
    // a count of learning and learned words
    private var learningAndLearnedWordsCount: LiveData<Int>? = null
    private var challengingWordsCount: LiveData<Int>? = null

    // expose learning and learned words count data
    fun getLearningAndLearnedWordsCount(): LiveData<Int>?{
        if(learningAndLearnedWordsCount == null){
            learningAndLearnedWordsCount = wordsDatabase.getLearningAndLearnedWordsCount()
        }
        return learningAndLearnedWordsCount
    }
    // expose challenging words count data
    fun getChallengingWordsCount(): LiveData<Int>?{
        if(challengingWordsCount == null){
            challengingWordsCount = wordsDatabase.getChallengingWordsCount()
        }
        return challengingWordsCount
    }
}
