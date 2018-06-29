package com.pedromassango.banzo.data.models

import com.pedromassango.banzo.enums.LanguageLevels

/**
 * Created by Pedro Massango on 6/23/18.
 */
class Level(var level: String,
            var code: LanguageLevels,
            var isSelected: Boolean = false)