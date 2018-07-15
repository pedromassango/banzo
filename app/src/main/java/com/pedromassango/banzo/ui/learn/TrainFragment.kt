package com.pedromassango.banzo.ui.learn

import android.graphics.Typeface
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.RelativeSizeSpan
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.pedromassango.banzo.LearningActivity
import com.pedromassango.banzo.R
import com.pedromassango.banzo.data.models.Word
import kotlinx.android.synthetic.main.train_fragment.*
import kotlinx.android.synthetic.main.train_fragment.view.*

/**
 * Fragment to memorise the words of the day.
 */
class TrainFragment : Fragment() {

    companion object {
        const val KEY_WORDS = "com.pedromassango.banzo.ui.learn.KEY_WORDS"

        fun newInstance(words: List<Word>): TrainFragment {
            // convert List to ArrayList (bundle allow only ArrayList)
            val tmp = arrayListOf<Word>()
            tmp.addAll(words)

            val instance = TrainFragment()
            val b = Bundle().apply {
                putParcelableArrayList(KEY_WORDS, tmp)
            }
            instance.arguments = b

            return instance
        }
    }

    private lateinit var words: List<Word>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val v = inflater.inflate(R.layout.train_fragment, container, false)

        with(v) {
            // on ready click, call next fragment
            btn_ready.setOnClickListener {
                (activity as LearningActivity)
                        .nextFragment()
            }
        }
        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // get words from arguments
        words = arguments!!.getParcelableArrayList(KEY_WORDS)

        val shuffledList = words.shuffled()
        val w1 = shuffledList[0]
        val w2 = shuffledList[1]
        val w3 = shuffledList[2]
        val w4 = shuffledList[3]

        // show learning words with a stylized translation
        tv_word1.text = stylizeText(w1.translation, w1.ptWord)
        tv_word2.text = stylizeText(w2.translation, w2.ptWord)
        tv_word3.text = stylizeText(w3.translation, w3.ptWord)
        tv_word4.text = stylizeText(w4.translation, w4.ptWord)
    }

    /**
     * Stylize the string. just bold the translation text.
     * @param source to be stylized.
     * @return the stylized text
     */
    private fun stylizeText(translation: String, ptWord: String): SpannableString {
        val source = String.format("%s\n%s", translation, ptWord)

        val stylizedText = SpannableString(source)
        stylizedText.setSpan(
                StyleSpan(Typeface.BOLD),
                0, translation.length + 1,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )
        stylizedText.setSpan(
                RelativeSizeSpan(1.5f),
                0, translation.length + 1,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )
        return stylizedText
    }
}
