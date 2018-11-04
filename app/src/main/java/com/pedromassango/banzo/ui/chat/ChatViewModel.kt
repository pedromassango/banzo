package com.pedromassango.banzo.ui.chat

import androidx.lifecycle.*
import com.pedromassango.banzo.data.CommentsRepository
import com.pedromassango.banzo.data.models.Comment
import com.pedromassango.banzo.data.models.CommentsLoadResult
import com.pedromassango.banzo.data.preferences.PreferencesHelper

class ChatViewModel(private val prefs: PreferencesHelper,
                    private val commentsRepository: CommentsRepository) : ViewModel() {

    // comments repository
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
     * Send a comment to server
     */
    fun onSendCommentClick(text: String) {

        // if there is no text, do not send the comment
        if(text.isEmpty()) { return }

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
