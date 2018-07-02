package com.pedromassango.banzo.ui.learned

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
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
import com.pedromassango.banzo.ui.MyDividerDecoration
import kotlinx.android.synthetic.main.learned_fragment.*
import kotlinx.android.synthetic.main.learned_fragment.view.*
import timber.log.Timber

class LearnedFragment : Fragment() {

    companion object {
        fun newInstance() = LearnedFragment()
    }

    private lateinit var viewModel: LearnedViewModel
    private val wordsAdapter: WordsAdapter = lazy {
        Timber.i("setting up wordsAdapter...")
        WordsAdapter()
    }.value

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
            // set data in adapter
            learnedWords?.let {
                Timber.i("showing learned words...")
                wordsAdapter.add(it)
            }
        })
    }

}
