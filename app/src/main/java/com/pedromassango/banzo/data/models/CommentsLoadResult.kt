package com.pedromassango.banzo.data.models

import androidx.lifecycle.LiveData

/**
 * A class tha contains LiveData<List<Comment>> and a
 * LiveData<List<String>> containing network errors state.
 */
class CommentsLoadResult(
        val data: LiveData<List<Comment>>,
        val networkErrors: LiveData<String>
)