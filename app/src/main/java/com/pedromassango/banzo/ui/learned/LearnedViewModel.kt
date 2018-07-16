package com.pedromassango.banzo.ui.learned

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pedromassango.banzo.data.WordsDatabase
import com.pedromassango.banzo.data.models.Word
import timber.log.Timber

class LearnedViewModel : ViewModel() {
    private val wordsDatabase = WordsDatabase.getInstance().wordDAO
    private var learnedWords: LiveData<List<Word>>? = null

    fun getLearnedWords(): LiveData<List<Word>>? {
        Timber.i("getLearnedWords()")

        if(learnedWords == null){
            learnedWords = MutableLiveData()
            learnedWords = wordsDatabase.getLearnedAndLearningWords()
        }

        return learnedWords
    }
}
