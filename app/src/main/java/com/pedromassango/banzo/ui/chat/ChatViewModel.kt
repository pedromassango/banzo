package com.pedromassango.banzo.ui.chat

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ChatViewModel : ViewModel() {
    private val clubId = MutableLiveData<String>()

    /**
     * set the selected club id to a liveData
     */
    fun setClubId(mClubId: String){
        clubId.postValue( mClubId)
    }


}
