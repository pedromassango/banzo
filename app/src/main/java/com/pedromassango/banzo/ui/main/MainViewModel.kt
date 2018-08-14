package com.pedromassango.banzo.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.pedromassango.banzo.data.WordsDatabase


class MainViewModel : ViewModel() {
    // database
    private val wordsDatabase = WordsDatabase.getInstance().wordDAO
    // a count of learning and learned words
    private var learningAndLearnedWordsCount: LiveData<Int>? = null
    private var learnedWordsCount: LiveData<Int>? = null
    private var challengingWordsCount: LiveData<Int>? = null
    private var authState = MutableLiveData<Boolean>()

    init {
        // Listen auth state
        FirebaseAuth.getInstance()
                .addAuthStateListener {
                    when(it.currentUser != null){
                        true -> authState.setValue(true)
                        false -> authState.setValue(false)
                    }
                }
    }

    /**
     *  Expose learning and learned words count data
     */
    fun getLearningAndLearnedWordsCount(): LiveData<Int>?{
        if(learningAndLearnedWordsCount == null){
            learningAndLearnedWordsCount = wordsDatabase.getLearningAndLearnedWordsCount()
        }
        return learningAndLearnedWordsCount
    }

    /**
     * Expose challenging words count data
     */
    fun getChallengingWordsCount(): LiveData<Int>?{
        if(challengingWordsCount == null){
            challengingWordsCount = wordsDatabase.getChallengingWordsCount()
        }
        return challengingWordsCount
    }

    /**
     * To notify UI when the user is logged in or not
     */
    fun getAuthState(): LiveData<Boolean>{
        return authState
    }

    /**
     * This function, retrieve the number of learned words.
     */
    fun getLearnedWordsCount(): LiveData<Int>?{
        if(learnedWordsCount == null){
            learnedWordsCount = wordsDatabase.getLearnedWords()
        }
        return learnedWordsCount
    }
}
