package com.pedromassango.banzo.ui.main


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.pedromassango.banzo.R
import com.pedromassango.banzo.data.preferences.PreferencesHelper
import kotlinx.android.synthetic.main.component_learned_words.*
import kotlinx.android.synthetic.main.component_learning_words.*
import timber.log.Timber
import android.animation.ValueAnimator
import android.widget.Toast
import androidx.navigation.findNavController
import com.google.android.gms.ads.AdRequest
import com.pedromassango.banzo.extras.runOnFree
import kotlinx.android.synthetic.free.fragment_statistic.view.*
import kotlinx.android.synthetic.main.component_learned_level.*


/**
 * A fragment to show user learn statistic.
 *
 */
class StatisticFragment : Fragment() {

    companion object {
        const val TEXT_ANIMATION_DURATION = 2500
    }

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_statistic, container, false)

        with(v){
            // setup ads
            runOnFree {
                val adRequest = AdRequest.Builder().build()
                adView_statistic.loadAd(adRequest)
            }
        }
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // button train difficult words
        btn_train_challenging_words.setOnClickListener {
            //Start activity to learn challenging words
            it.findNavController().navigate(R.id.action_statisticFragment_to_learningActivity)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(MainViewModel::class.java)

        var learnedAndLearningWordsObserver: Observer<Int>? = null
        val learnedWordsObserver: Observer<Int>?
        var challengingWordsObserver: Observer<Int>? = null

        // get and show the number of learned and learning words
        learnedAndLearningWordsObserver = Observer { learnedAndLearningWordsCount ->
            viewModel.getLearningAndLearnedWordsCount()?.removeObserver(learnedAndLearningWordsObserver!!)

            Timber.i("Number of learned and learning words: $learnedAndLearningWordsCount")

            // calculate average of learned words then show it
            val totalWords = PreferencesHelper().totalWordsToLearn
            val progress = ((learnedAndLearningWordsCount * 100) / totalWords).toFloat()

            // show total learned and learning words
            val animator = ValueAnimator.ofInt(0, learnedAndLearningWordsCount)
            animator.duration = TEXT_ANIMATION_DURATION.toLong()
            animator.addUpdateListener{ anim ->
                tv_learned_words_count?.text = anim.animatedValue.toString()
            }
            animator.start()

            // show learning progress
            val value = "${progress.toInt()}%"
            tv_progress_learnig_average.text = value
            // only use animation if progress is more than 5.
            when(progress >= 5) {
                true -> progress_learnig_average.setProgressWithAnimation(progress, TEXT_ANIMATION_DURATION)
                false -> progress_learnig_average.progress = progress
            }
        }

        // show the number of learned words
        learnedWordsObserver = Observer {learnedWordsCount ->

            // calculate average of learned words
            val totalWords = PreferencesHelper().totalWordsToLearn
            val progress = ((learnedWordsCount * 100) / totalWords).toFloat()

            // show progress
            progress_learned_words.progress = progress
            // show learned words count
            tv_progress_learned_words.text = learnedWordsCount.toString()
        }

        // if there is challenging words, we need to show an alert.
        challengingWordsObserver = Observer {challengingWordsCount ->
            if(challengingWordsCount >= 1){
                tv_challenging_words.text = String.format(getString(R.string.palavras_desafiadoras_encontradas),
                        challengingWordsCount)

                // make the view visible
                layout_challenging_words.visibility = View.VISIBLE
            }
        }

        // start looking for data
        viewModel.getLearnedWordsCount()?.observe(this, learnedWordsObserver)
        viewModel.getLearningAndLearnedWordsCount()?.observe(this, learnedAndLearningWordsObserver)
        // start observing challenging words count
        viewModel.getChallengingWordsCount()?.observe(this@StatisticFragment, challengingWordsObserver)
    }

}
