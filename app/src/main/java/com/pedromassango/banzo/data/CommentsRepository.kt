package com.pedromassango.banzo.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pedromassango.banzo.data.models.Comment
import com.pedromassango.banzo.data.models.CommentsLoadResult

class CommentsRepository(
        private val service: CommentsService,
        private val cache: CommentsLocalCache
) {

    private val networkError = MutableLiveData<String>()

    fun getComments(clubId: String): CommentsLoadResult{
        val commentsResult = cache.getAll(clubId)

        // get all comments from server
        // if get result, save in local cache
        // if error, expose it
        service.getAll(clubId,
                { result -> cache.insert(result) },
                { error -> networkError.postValue(error) })

        return CommentsLoadResult(null, networkError)
    }

    /**
     * Load local comments by club id
     */
    fun loadCache(clubId: String): LiveData<List<Comment>>{
        return cache.getAll(clubId)
    }

    /**
     * send a comment to remote server
     */
    fun saveComment(clubId: String,
                    comment: Comment,
                    onSuccess: ()-> Unit,
                    onError: (String?) -> Unit){

        // send comment and insert it in cache, when succeeded
        service.send(clubId, comment,
                { cache.insert( it) ; onSuccess() },
                { error -> onError(error) })
    }

}