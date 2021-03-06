package com.pedromassango.banzo.ui.learn

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.pedromassango.banzo.data.AppDatabase
import com.pedromassango.banzo.data.WordDAO
import com.pedromassango.banzo.data.models.Word
import timber.log.Timber

class LearnViewModel(private val wordsDatabase: WordDAO) : ViewModel() {

    // Words database
    //private val wordsDatabase = AppDatabase.getInstance().wordDAO

    // learning words
    private var learningWords: LiveData<List<Word>>? = null
    // fake words
    private var fakeWords: LiveData<List<Word>>? = null

    fun getLearningWords(): LiveData<List<Word>>? {
        if(learningWords == null) {
            Timber.i("Getting learning words...")
            learningWords = wordsDatabase.getLearningWords()
        }

        Timber.i("Getting learning words - done")
        return learningWords
    }

    fun getFakeWords(): LiveData<List<Word>>?{
        if(fakeWords == null){
            Timber.i("Getting fake words...")
            fakeWords = wordsDatabase.getFakeWords()
        }
        return fakeWords
    }

    fun update(word: Word){
        wordsDatabase.update(word)
    }
}
