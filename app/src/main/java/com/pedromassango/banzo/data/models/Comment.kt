package com.pedromassango.banzo.data.models

/**
 * Created by Pedro Massango on 8/16/18.
 */
class Comment(
        var id: String = "",
        var mText: String,
        var author: String,
        var authorPhotoUrl: String = "",
        var timestamp: Long = 0
)