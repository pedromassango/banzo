package com.pedromassango.banzo.ui.learn

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.pedromassango.banzo.LearningActivity
import com.pedromassango.banzo.R
import com.pedromassango.banzo.data.models.Word
import kotlinx.android.synthetic.main.writing_fragment.*
import kotlinx.android.synthetic.main.writing_fragment.view.*

class WritingFragment : Fragment(), View.OnClickListener {

    companion object {
        const val KEY_WORD = "KEY_WORD"
        const val KEY_LAST_FRAGMENT = "KEY_LAST_FRAGMENT"
        fun newInstance(word: Word, isLastFragment: Boolean = false): WritingFragment {
            val instance = WritingFragment()
            val b = Bundle().apply {
                putParcelable(KEY_WORD, word)
                putBoolean(KEY_LAST_FRAGMENT, isLastFragment)
            }
            instance.arguments = b

            return instance
        }
    }

    private lateinit var viewModel: LearnViewModel
    private lateinit var iWriteFragmentListener: IWriteFragmentListener

    private var wordToLearn: Word? = null

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
        checkNotNull(wordToLearn)
        tv_word_to_learn.text = wordToLearn!!.translation
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        // get fragment listener from activity
        iWriteFragmentListener = (context as IWriteFragmentListener)
    }

    private var canClick = true
    override fun onClick(textView: View?) {
        // typed text
        val translation = edt_response.text.toString().trim()
        // if already click or typed text is empty, do nothing
        if (!canClick || translation.isEmpty()) {
            return
        }
        canClick = false

        // handle hit or missed this word
        textView?.postDelayed({
            when(translation.trim().equals(wordToLearn!!.ptWord.trim(), true)){
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

            // change text color
            edt_response.setTextColor(ResourcesCompat.getColor(resources, R.color.white, null))
        }, LearningActivity.PAUSE_SHOW_ANSWER_TIME)


        // sleep a while, and notify activity if it is the last fragment
        // or got to next fragment
        // running on main thread
        textView?.postDelayed({

            // if last fragment, notify activity, else go to next fragment
            when (arguments!!.getBoolean(KEY_LAST_FRAGMENT)) {
                true -> iWriteFragmentListener.onLearnWritingFinished()
                false -> (activity!! as LearningActivity).nextFragment()
            }
        }, LearningActivity.PAUSE_SCREEN_TIME)

    }

}
