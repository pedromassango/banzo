package com.pedromassango.banzo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.PagerAdapter
import com.pedromassango.banzo.data.models.Word
import com.pedromassango.banzo.ui.learn.*
import kotlinx.android.synthetic.main.learn_activity.*
import timber.log.Timber

class LearningActivity : AppCompatActivity(),
        IReadFragmentListener,
        IWriteFragmentListener
        , Observer<List<Word>> {

    companion object {
        // Time to wait before show the next fragment
        const val PAUSE_SCREEN_TIME = 1700L
        // Time to pause before show the right answer
        const val PAUSE_SHOW_ANSWER_TIME = 250L
    }

    private lateinit var viewModel: LearnViewModel
    // store times tha the user learned by reading
    private var learnReadingTimes = 0
    // store times tha the user learned by writing
    private var learnWritingTimes = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.learn_activity)
        setSupportActionBar(toolbar)

        // set margin on viewPager to preview other fragments
        learn_viewpager.clipToPadding = false
        learn_viewpager.setPadding(60, 0, 60, 0)
        learn_viewpager.pageMargin = 10

        // back button click
        toolbar.setNavigationOnClickListener {
            this.onBackPressed()
        }

        // get viewModel
        viewModel = ViewModelProviders.of(this).get(LearnViewModel::class.java)
        // get words to learn
        viewModel.getLearningWords()?.observe(this, this)
        // pre-load fake words from database
        viewModel.getFakeWords()
    }

    /**
     * Method to receive words that need to be learned at this time.
     * @param words the words to be learned
     */
    override fun onChanged(words: List<Word>?) {
        // remove observer, we need to observe just one time
        viewModel.getLearningWords()?.removeObserver(this)

        // shuffle the list
        val shuffledWords = words?.shuffled()

        // create one instance of train fragment
        // and four of learning fragment
        // with an word to learn,
        // and set in list
        // if user already learn by reading two times
        // they should learn now by writing
        val fragments: ArrayList<Fragment> = when (learnReadingTimes < 2) {
            true -> arrayListOf(
                    TrainFragment.newInstance(), // to train the words
                    ReadingFragment.newInstance(shuffledWords!![0]),
                    ReadingFragment.newInstance(shuffledWords[1]),
                    ReadingFragment.newInstance(shuffledWords[3]),
                    ReadingFragment.newInstance(shuffledWords[2], true)
            )
            false -> arrayListOf(
                    WritingFragment.newInstance(shuffledWords!![0]),
                    WritingFragment.newInstance(shuffledWords[1]),
                    WritingFragment.newInstance(shuffledWords[3]),
                    WritingFragment.newInstance(shuffledWords[2], true)
            )
        }

        // show fragments
        learn_viewpager.adapter = MyPagerAdapter(supportFragmentManager, fragments)
    }

    /**
     * Called when the last learn reading fragment was finished
     */
    override fun onLearnReadingFinished() {
        Timber.i("onLearnReadingFinished() last: $learnReadingTimes")

        learnReadingTimes += 1
        Timber.i("onLearnReadingFinished() current: $learnReadingTimes")

        viewModel.getLearningWords()?.observe(this, this)
    }

    /**
     * Called when the last learn writing fragment was finished
     */
    override fun onLearnWritingFinished() {
        Timber.i("onLearnWritingFinished() last: $learnWritingTimes")

        learnWritingTimes += 1
        Timber.i("onLearnWritingFinished() current: $learnWritingTimes")

        // if user learned by writing two times, finish activity
        // else, learn again by writing
        if (learnWritingTimes == 2) {
            this.finish()
        } else {
            viewModel.getLearningWords()?.observe(this, this)
        }
    }

    /**
     * Called when the user has learned an word.
     * Here we need to check if the user hit the word or missed it.
     * @param learnedWord the word tha the user has leaned
     * @param hit the state of the learned word
     */
    override fun onLearnWordResult(learnedWord: Word?, hit: Boolean) {
        Timber.i("Learned word: ${learnedWord?.translation} & hit: $hit")

        when(hit){
            true -> learnedWord!!.hitCounter += 1
            else -> learnedWord!!.failCount += 1
        }

        viewModel.update(learnedWord!!)
    }

    /**
     * Switch to the next fragment
     */
    fun nextFragment() {
        // if last fragment do nothing, else go to next fragment
        if (learn_viewpager.adapter!!.count == learn_viewpager.currentItem) {
            return
        }

        learn_viewpager.setCurrentItem(
                learn_viewpager.currentItem + 1,
                true
        )
    }

    /**
     * Adapter class to adapt all learning fragments.
     * @param fragmentManager manager for fragments
     * @param fragments fragments to show.
     */
    inner class MyPagerAdapter(fragmentManager: FragmentManager,
                               private val fragments: ArrayList<Fragment>) :
            FragmentStatePagerAdapter(fragmentManager) {

        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

        override fun getCount(): Int {
            return fragments.size
        }

    }

}
