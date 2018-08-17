package com.pedromassango.banzo.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pedromassango.banzo.data.models.Comment

@Dao
interface CommentDao {

    /**
     * Insert a comment in database
     * @param comment to insert in database
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(comment: Comment)

    /**
     * Insert a set of comments in database
     * @param comments to be inserted in database.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addAll(comments: List<Comment>)

    /**
     * Get all comments from the giving club id.
     * @param club_id the club id to load all comments.
     */
    @Query("SELECT * FROM comments_table WHERE clubId == :club_id")
    fun getAllByClub(club_id: String): LiveData<List<Comment>>

}