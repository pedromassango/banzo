package com.pedromassango.banzo.ui.chat

import androidx.lifecycle.*
import com.pedromassango.banzo.data.AppDatabase
import com.pedromassango.banzo.data.CommentsLocalCache
import com.pedromassango.banzo.data.CommentsRepository
import com.pedromassango.banzo.data.CommentsService
import com.pedromassango.banzo.data.models.Comment
import com.pedromassango.banzo.data.models.CommentsLoadResult
import com.pedromassango.banzo.data.preferences.PreferencesHelper

class ChatViewModel : ViewModel() {

    // comments repository
    private val commentsService = CommentsService()
    private val prefs = PreferencesHelper()
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
        // load from local cache
        commentsRepository.loadCache(clubId.value!!)
    }
    // updated when a comment is sent
    var sendCommentResult = MutableLiveData<Boolean>()

    /**
     * set the selected club id to a liveData
     * calling this function will start fetch comments from database
     */
    fun setClubId(mClubId: String) = clubId.postValue( mClubId)

    /**
     * Send a comment
     */
    fun sendComment(text: String) {
        val comment = Comment(
                mText = text,
                author = prefs.username,
                authorPhotoUrl = prefs.photoUrl,
                clubId = clubId.value!!,
                timestamp = System.currentTimeMillis()
        )

        // send comment
        commentsRepository.saveComment(clubId.value!!, comment,
                { sendCommentResult.postValue(true)},
                { sendCommentResult.postValue(false)}
        )
    }
}
