package com.pedromassango.banzo.data

import androidx.lifecycle.MutableLiveData
import com.pedromassango.banzo.data.models.CommentsLoadResult

class CommentsRepository(
        private val service: CommentsService,
        private val cache: CommentsLocalCache
) {

    private val networkError = MutableLiveData<String>()

    fun getComments(clubId: String): CommentsLoadResult{

        // get all from cache
        val data = cache.getAll( clubId)

        // get all comments from server
        // if get result, save in local cache
        // if error, expose it
        service.getAll(clubId,
                { result -> cache.insert(result) },
                { error -> networkError.postValue(error) })

        return CommentsLoadResult(data, networkError)
    }

}