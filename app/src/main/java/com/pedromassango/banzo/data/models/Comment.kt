package com.pedromassango.banzo.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by Pedro Massango on 8/16/18.
 */
@Entity(tableName = "comments_table")
class Comment(
        @PrimaryKey
        var id: String = "",
        var mText: String,
        var author: String,
        var authorPhotoUrl: String = "",
        var clubId: String = "",
        var timestamp: Long = 0
)