package com.pedromassango.banzo.data.models

import com.pedromassango.banzo.enums.LanguagestTypes

/**
 * Created by Pedro Massango on 6/23/18.
 */
class Language(var languageName: String,
               var type: LanguagestTypes = LanguagestTypes.ENGLISH,
               var isSelected: Boolean = false)