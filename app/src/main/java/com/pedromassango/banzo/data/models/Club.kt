package com.pedromassango.banzo.data.models

import androidx.annotation.DrawableRes

class Club(
        val id: String,
        var name: String,
        var description: String,
        @DrawableRes
        var image: Int
)