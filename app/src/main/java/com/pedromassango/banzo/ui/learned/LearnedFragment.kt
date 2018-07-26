package com.pedromassango.banzo.ui.learned

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pedromassango.banzo.R
import com.pedromassango.banzo.data.preferences.PreferencesHelper
import com.pedromassango.banzo.enums.LanguagestTypes
import com.pedromassango.banzo.ui.MyDividerDecoration
import kotlinx.android.synthetic.main.learned_fragment.*
import kotlinx.android.synthetic.main.learned_fragment.view.*
import timber.log.Timber
import java.util.*

class LearnedFragment : Fragment() {

    companion object {
        fun newInstance() = LearnedFragment()
    }

    private lateinit var viewModel: LearnedViewModel
    private lateinit var tts: TextToSpeech
    private val languageToLearn = PreferencesHelper().getLangToLearn()
    private val wordsAdapter: WordsAdapter = lazy {
        Timber.i("setting up wordsAdapter...")
        WordsAdapter(this)
    }.value

    /**
     * Function to speak the learning word.
     * ATT: this will speak only if the learning idiom
     * is ENGLISH.
     */
    fun speak(text: String){
        // We speak only if language to learn is english
        if(languageToLearn != LanguagestTypes.ENGLISH){
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, text.trim())
        }else{
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setup TextToSpeech
        tts = TextToSpeech(context, TextToSpeech.OnInitListener { Timber.i("TTS initialized") })
        tts.language = Locale.US
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val v = inflater.inflate(R.layout.learned_fragment, container, false)

        with(v) {
            recycler_learned_words.layoutManager = LinearLayoutManager(context)
            recycler_learned_words.addItemDecoration( MyDividerDecoration(context))
            recycler_learned_words.adapter = wordsAdapter
        }

        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(LearnedViewModel::class.java)
        viewModel.getLearnedWords()?.observe(this, Observer { learnedWords ->
            Timber.i("learned words ready: ${learnedWords?.size}")

            // remove progress bar
            progress_learned.visibility = View.GONE

            // If list is empty, show the message
            // else hide it and stop the execution
            tv_empty_data.visibility =
                    if(learnedWords!!.isEmpty())
                        View.VISIBLE
                    else { View.GONE }

            // set data in adapter
            learnedWords.let {
                Timber.i("showing learned words...")
                wordsAdapter.add(it)
            }
        })
    }

}
