package com.pedromassango.banzo.data

import androidx.lifecycle.LiveData
import com.pedromassango.banzo.data.models.Comment
import timber.log.Timber

/**
 * This class handle all comments query to a remote server.
 */
class CommentsLocalCache(
        private val commentsDao: CommentDao
) {

    fun insert(comments: List<Comment>){
        commentsDao.addAll(comments)
        Timber.i("inserting ${comments.size} comments")
        //insertfinished()
    }

    /**
     * Load all comments from a club.
     * Fetch all comments from Room Database that match with the param.
     * @param club_id the club id to get comments
     */
    fun getAll(club_id: String): LiveData<List<Comment>>{
        return commentsDao.getAllByClub( club_id)
    }
}