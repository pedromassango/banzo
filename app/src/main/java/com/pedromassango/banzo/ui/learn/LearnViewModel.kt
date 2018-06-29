package com.pedromassango.banzo.ui.learn

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.pedromassango.banzo.data.WordsDatabase
import com.pedromassango.banzo.data.models.Word
import timber.log.Timber

class LearnViewModel : ViewModel() {

    // Words database
    private val wordsDatabase = WordsDatabase.getInstance().wordDAO

    // learnig words
    private var learningWords: MutableLiveData<List<Word>>? = null
    // fake words
    private var fakeWords: LiveData<List<Word>>? = null

    fun getLearningWords(): MutableLiveData<List<Word>>? {
        if(learningWords == null) {
            Timber.i("Getting learning words...")
            learningWords = MutableLiveData()
            wordsDatabase.getLearningWords()?.observeForever {
                // set this words in learning state
                it?.forEach{
                    it.learning = true
                    wordsDatabase.update(it)
                }
                // expose the data
                learningWords?.postValue(it)
                Timber.i("Done...")
            }
        }

        return learningWords
    }

    fun getFakeWords(): LiveData<List<Word>>?{
        if(fakeWords == null){
            fakeWords = MutableLiveData()
            fakeWords = wordsDatabase.getFakeWords()
        }
        return fakeWords
    }
}
