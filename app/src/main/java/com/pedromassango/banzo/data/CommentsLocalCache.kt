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

    fun insert(comment: Comment){
        commentsDao.add(comment)
        Timber.i("inserting comment ID: ${comment.id}")
        //insertfinished()
    }

    fun insert(comments: List<Comment>){

        // TODO: removing all local comments, to repopulate
        deleteAll()

        // repopulate database
        comments.forEach { insert(it) }

        Timber.i("inserting ${comments.size} comments")
    }

    /**
     * Load all comments from a club.
     * Fetch all comments from Room Database that match with the param.
     * @param club_id the club id to get comments
     */
    fun getAll(club_id: String): LiveData<List<Comment>>{
        val result = commentsDao.getAllByClub(club_id)

        Timber.i("local comments: ${result.value?.size}")

        return result
    }

    fun deleteAll(){
        commentsDao.delete()
    }
}