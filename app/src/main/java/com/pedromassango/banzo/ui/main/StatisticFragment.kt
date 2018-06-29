package com.pedromassango.banzo.ui.main


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.pedromassango.banzo.R
import kotlinx.android.synthetic.main.component_learned_words.*
import timber.log.Timber


/**
 * A fragment to show user learn statistic.
 *
 */
class StatisticFragment : Fragment() {

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_statistic, container, false)
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

            // show data
            tv_learned_words_count.text = learnedWordsCount.toString()
        }

        // start looking for data
        viewModel.getLearningAndLearnedWordsCount()?.observe(this, learnedWordsObserver)
    }

}
