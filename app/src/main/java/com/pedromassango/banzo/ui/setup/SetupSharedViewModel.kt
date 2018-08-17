package com.pedromassango.banzo.ui.setup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pedromassango.banzo.data.AppDatabase
import com.pedromassango.banzo.data.models.Language
import com.pedromassango.banzo.data.models.Level
import com.pedromassango.banzo.data.models.Word

class SetupSharedViewModel : ViewModel() {

    // Words database
    private val wordsDatabase = AppDatabase.getInstance()

    private var selectedLanguage = MutableLiveData<Language>()
    private var selectedLanguageLevel = MutableLiveData<Level>()

    fun selectLanguage(language: Language){
        selectedLanguage.postValue(language)
    }

    fun selectLevel(level: Level){
        selectedLanguageLevel.postValue( level)
    }



    fun getSelectedLanguage(): LiveData<Language>{
        return selectedLanguage
    }

    fun saveWord(word: Word) {
        wordsDatabase.wordDAO
                .add(word)
    }


}
