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
import com.google.android.gms.ads.AdRequest
import com.pedromassango.banzo.extras.runOnFree
import kotlinx.android.synthetic.free.fragment_statistic.*
import kotlinx.android.synthetic.free.fragment_statistic.view.*
import kotlinx.android.synthetic.main.fragment_statistic.view.*


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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(MainViewModel::class.java)

        var learnedWordsObserver: Observer<Int>? = null

        // get and show the number of learned and learning words
        learnedWordsObserver = Observer { learnedWordsCount ->
            viewModel.getLearningAndLearnedWordsCount()?.removeObserver(learnedWordsObserver!!)

            Timber.i("Number of learned and learning words: $learnedWordsCount")

            // calculate average of learned words then show it
            val totalWords = PreferencesHelper().totalWordsToLearn
            val progress = ((learnedWordsCount * 100) / totalWords).toFloat()

            // show total learned and learning words
            val animator = ValueAnimator.ofInt(0, learnedWordsCount)
            animator.duration = TEXT_ANIMATION_DURATION.toLong()
            animator.addUpdateListener{ anim ->
                tv_learned_words_count.text = anim.animatedValue.toString()
            }
            animator.start()

            // show learning progress
            tv_progress_learnig_average.text = progress.toString()
            progress_learnig_average.setProgressWithAnimation( progress, TEXT_ANIMATION_DURATION)
        }

        // start looking for data
        viewModel.getLearningAndLearnedWordsCount()?.observe(this, learnedWordsObserver)
    }

}
