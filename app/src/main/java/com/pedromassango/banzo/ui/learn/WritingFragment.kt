package com.pedromassango.banzo.ui.learn

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.pedromassango.banzo.ui.LearningActivity
import com.pedromassango.banzo.R
import com.pedromassango.banzo.data.models.Word
import kotlinx.android.synthetic.main.writing_fragment.*
import kotlinx.android.synthetic.main.writing_fragment.view.*

class WritingFragment : Fragment(), View.OnClickListener {

    companion object {
        const val KEY_WORD = "KEY_WORD"
        const val KEY_REVERSED = "KEY_REVERSED"
        const val KEY_LAST_FRAGMENT = "KEY_LAST_FRAGMENT"
        fun newInstance(word: Word, reversed: Boolean = false, isLastFragment: Boolean = false): WritingFragment {
            val instance = WritingFragment()
            val b = Bundle().apply {
                putParcelable(KEY_WORD, word)
                putBoolean(KEY_REVERSED, reversed)
                putBoolean(KEY_LAST_FRAGMENT, isLastFragment)
            }
            instance.arguments = b

            return instance
        }
    }

    private lateinit var viewModel: LearnViewModel
    private lateinit var iWriteFragmentListener: IWriteFragmentListener

    private lateinit var wordToLearn: Word
    private var reverseTranslation = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val v = inflater.inflate(R.layout.writing_fragment, container, false)

        with(v) {

            // request editText cursor
            edt_response.requestFocus()

            // attach click listener on submit button
            btn_submit_response.setOnClickListener(this@WritingFragment)
        }
        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(LearnViewModel::class.java)

        // Get and show the learning word
        wordToLearn = arguments!!.getParcelable(KEY_WORD)
        reverseTranslation = arguments!!.getBoolean(KEY_REVERSED)
        checkNotNull(wordToLearn)
        tv_word_to_learn.text = if(reverseTranslation) wordToLearn.ptWord else wordToLearn.translation
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        // get fragment listener from activity
        iWriteFragmentListener = (context as IWriteFragmentListener)
    }

    private var canClick = true
    override fun onClick(textView: View?) {
        // typed text
        val translation = edt_response.text.toString()
        // if already click or typed text is empty, do nothing
        if (!canClick || translation.trim().isEmpty()) {
            return
        }
        canClick = false

        // check if is the right answer
        val rightAnswer = translation.trim().equals(wordToLearn.ptWord.trim(), true) ||
                translation.trim().equals(wordToLearn.translation.trim(), true)

        // handle hit or missed this word
        textView?.postDelayed({
            when(rightAnswer){
                true ->{
                    // user hit that word
                    iWriteFragmentListener.onLearnWordResult(wordToLearn, true)
                    edt_response.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.green, null))
                }
                false -> {
                    // user missed this word
                    iWriteFragmentListener.onLearnWordResult(wordToLearn, false)
                    edt_response.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.red, null))
                }
            }

            // Speak the learning word to memorize the pronounce
            val text = when(!reverseTranslation) {
                true -> tv_word_to_learn.text.toString()
                else -> wordToLearn.translation
            }
            // start speaking
            (activity as LearningActivity).speak(text)

            // change text color
            edt_response.setTextColor(ResourcesCompat.getColor(resources, R.color.white, null))
        }, LearningActivity.PAUSE_SHOW_ANSWER_TIME)


        // sleep a while, and notify activity if it is the last fragment
        // or got to next fragment
        // running on main thread
        textView?.postDelayed({

            // if last fragment, notify activity, else go to next fragment
            when (arguments!!.getBoolean(KEY_LAST_FRAGMENT)) {
                true -> iWriteFragmentListener.onLearnWritingFinished(reverseTranslation)
                false -> (activity!! as LearningActivity).nextFragment()
            }
        }, LearningActivity.PAUSE_SCREEN_TIME)

    }

}
