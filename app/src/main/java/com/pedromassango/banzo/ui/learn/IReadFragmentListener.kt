package com.pedromassango.banzo.ui.learn

import com.pedromassango.banzo.data.models.Word

/**
 * Created by Pedro Massango on 6/26/18.
 */
interface IReadFragmentListener {
    fun onLearnWordResult(learnedWord: Word?,
                          hit: Boolean
    )

    fun onLearnReadingFinished( reversed: Boolean)
}