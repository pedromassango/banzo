package com.pedromassango.banzo.ui.chat

import android.app.Application
import androidx.lifecycle.*
import com.pedromassango.banzo.data.AppDatabase
import com.pedromassango.banzo.data.CommentsLocalCache
import com.pedromassango.banzo.data.CommentsRepository
import com.pedromassango.banzo.data.CommentsService
import com.pedromassango.banzo.data.models.Comment
import com.pedromassango.banzo.data.models.CommentsLoadResult

class ChatViewModel : ViewModel() {

    // comments repository
    private val commentsService = CommentsService()
    private val appDatabase = AppDatabase.getInstance()
    private val commentsRepository = CommentsRepository(
            commentsService,
            CommentsLocalCache( appDatabase.commentsDao)
    )
    private val clubId = MutableLiveData<String>()

    private val commentsResult: LiveData<CommentsLoadResult> = Transformations.map(clubId) {
        commentsRepository.getComments(it)
    }

    // notified when errors occurs
    var errorEvent: LiveData<String> = Transformations.switchMap(commentsResult){
        it.networkErrors
    }
    // comments
    var commentsEvent: LiveData<List<Comment>> = Transformations.switchMap(commentsResult){
        it.data
    }

    /**
     * set the selected club id to a liveData
     * calling this function will start fetch comments from database
     */
    fun setClubId(mClubId: String) = clubId.postValue( mClubId)
}
