package com.pedromassango.banzo.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.pedromassango.banzo.data.AuthManager
import com.pedromassango.banzo.data.WordDAO
import com.pedromassango.banzo.data.preferences.PreferencesHelper


class MainViewModel(private val wordsDatabase: WordDAO,
                    private val authManager: AuthManager) : ViewModel() {
    // preferences
    private val preferencesHelper = PreferencesHelper()

    // a count of learning and learned words
    private var learningAndLearnedWordsCount: LiveData<Int>? = null
    private var learnedWordsCount: LiveData<Int>? = null
    private var challengingWordsCount: LiveData<Int>? = null
    private var learningWordsWithMoreHitsCount: LiveData<Int>? = null
    private var authState = MutableLiveData<Boolean>()
    private var errorEvent = MutableLiveData<String>()
    private var loginErrorEvent = MutableLiveData<String>()

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
     * To notify UI when auth error occurs
     */
    fun getAuthErrorState(): LiveData<String>{
        return loginErrorEvent
    }

    /**
     * The LiveData that will be notified when some error occours
     */
    fun getErrorEvent(): LiveData<String>{
        return errorEvent
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

    /**
     * Expose learning words with more hits than fails
     */
    fun getLearningWordsWithMoreHitsCount(): LiveData<Int>?{
        if(learningWordsWithMoreHitsCount == null){
            learningWordsWithMoreHitsCount = wordsDatabase.getLearningWordsWithMoreHitsCount()
        }
        return learningWordsWithMoreHitsCount
    }

    /**
     * Start Google auth
     * @param account the account to authenticate with.
     */
    fun authWithGoogle(account: GoogleSignInAccount) {
        // start google auth in server
        authManager
                .errorListener(loginErrorEvent)
                .authWithGoogle(account){ firebaseUser ->
                    // if not null user is logged in
                    authState.postValue( firebaseUser != null)
                }
    }
}
