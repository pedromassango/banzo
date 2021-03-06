package com.pedromassango.banzo.ui.learn

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.pedromassango.banzo.ui.LearningActivity
import com.pedromassango.banzo.R
import com.pedromassango.banzo.data.models.Word
import kotlinx.android.synthetic.main.reading_fragment.*
import kotlinx.android.synthetic.main.reading_fragment.view.*
import timber.log.Timber
import java.util.*
import android.speech.tts.TextToSpeech
import org.koin.androidx.viewmodel.ext.android.viewModel


class ReadingFragment : Fragment(), View.OnClickListener, TextToSpeech.OnInitListener {
    override fun onInit(p0: Int) {

    }

    companion object {
        // Time to wait before show the next fragment
        const val KEY_WORD = "KEY_WORD"
        const val KEY_REVERSED = "KEY_WORD_REVERSED"
        const val KEY_LAST_FRAGMENT = "KEY_LAST_FRAGMENT"
        fun newInstance(word: Word, reversed: Boolean = false, isLastFragment: Boolean = false): ReadingFragment {
            val instance = ReadingFragment()
            val b = Bundle().apply {
                putParcelable(KEY_WORD, word)
                putBoolean(KEY_REVERSED, reversed)
                putBoolean(KEY_LAST_FRAGMENT, isLastFragment)
            }
            instance.arguments = b

            return instance
        }
    }

    private val learnViewModel: LearnViewModel by viewModel()
    private lateinit var fakeWordsObserver: Observer<List<Word>>
    private lateinit var iReadFragmentListener: IReadFragmentListener
    private var optionsClickListener: View.OnClickListener? = null

    private lateinit var wordToLearn: Word
    private var reverseTranslation: Boolean = false
    private var validateChosedOption: Boolean = true
    private val textViews = arrayListOf<TextView>()
    // the position of right answer's textView
    private var rightAnswerPosition = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val v = inflater.inflate(R.layout.reading_fragment, container, false)

        with(v) {
            textViews.add(tv_option1)
            textViews.add(tv_option2)
            textViews.add(tv_option3)
            textViews.add(tv_option4)

            // set click listener on all testView options
            textViews.forEach {
                optionsClickListener?.let {listener ->
                    it.setOnClickListener(listener)
                }
            }

            // on ignore word, just skip to the next fragment
            btn_ignore_word.setOnClickListener {
                validateChosedOption = false
                val textView = textViews[rightAnswerPosition]
                onClick(textView)
            }
        }
        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // Get and show the learning word
        wordToLearn = arguments!!.getParcelable(KEY_WORD)
        reverseTranslation = arguments!!.getBoolean(KEY_REVERSED)
        checkNotNull(wordToLearn)
        tv_word_to_learn.text = if(reverseTranslation) wordToLearn.ptWord else wordToLearn.translation

        // A random position to set the right answer
        val random = Random()
        rightAnswerPosition = random.nextInt(textViews.size)

        // get and show fake words
        fakeWordsObserver = Observer {
            Timber.i("GetFakeWords()")
            learnViewModel.getFakeWords()?.removeObserver(fakeWordsObserver)

            // shuffle list
            val fakes = it?.shuffled()

            textViews.forEachIndexed { posix, _ ->
                Timber.i("TextViews()")
                // set the correct answer on first generated textView
                val generatedTextView = textViews[posix]
                when (posix == rightAnswerPosition) {
                    true -> generatedTextView.text = if(reverseTranslation) wordToLearn.translation else wordToLearn.ptWord
                    false -> generatedTextView.text = if(reverseTranslation) fakes!![posix].translation else fakes!![posix].ptWord
                }
            }
        }

        // observe fake words container
        learnViewModel.getFakeWords()?.observe(this, fakeWordsObserver)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        Timber.i("onAttach()")
        iReadFragmentListener = (context as IReadFragmentListener)
        optionsClickListener = this@ReadingFragment
    }

    private var canClick = true
    override fun onClick(textView: View?) {
        if (!canClick) {
            return
        }
        canClick = false
        // disable click to skip button
        btn_ignore_word.isClickable = false

        val translation = (textView as TextView).text

        // check if is the right answer
        val rightAnswer = translation.trim().toString().equals(wordToLearn.ptWord.trim(), true) ||
                translation.trim().toString().equals(wordToLearn.translation.trim(), true)

        // handle hit or missed this word
        textView.postDelayed({
            when (rightAnswer) {
                true -> {
                    // if chosen by user, make an hit
                    if(validateChosedOption) {
                        // user hit that word
                        iReadFragmentListener.onLearnWordResult(wordToLearn, true)
                    }
                    textView.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.green, null))
                }
                false -> {
                    // user missed this word
                    iReadFragmentListener.onLearnWordResult(wordToLearn, false)
                    textView.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.red, null))
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
            textView.setTextColor(ResourcesCompat.getColor(resources, R.color.white, null))
        }, LearningActivity.PAUSE_SHOW_ANSWER_TIME)


        // sleep a while, and notify activity if it is the last fragment
        // or got to next fragment
        // running on main thread
        textView.postDelayed({

            // call next fragment
            nextFragment()
        }, LearningActivity.PAUSE_SCREEN_TIME)
    }

    /**
     * If last fragment, notify activity or go to next fragment
     */
    private fun nextFragment(){
        optionsClickListener = null
        when (arguments!!.getBoolean(KEY_LAST_FRAGMENT)) {
            true -> iReadFragmentListener.onLearnReadingFinished(reverseTranslation)
            false -> (activity!! as LearningActivity).nextFragment()
        }
    }

}
