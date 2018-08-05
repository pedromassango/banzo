package com.pedromassango.banzo

import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.pedromassango.banzo.data.models.Word
import com.pedromassango.banzo.data.preferences.PreferencesHelper
import com.pedromassango.banzo.enums.LanguagestTypes
import com.pedromassango.banzo.extras.runOnFree
import com.pedromassango.banzo.ui.learn.*
import kotlinx.android.synthetic.main.activity_learn.*
import timber.log.Timber
import java.util.*

class LearningActivity : AppCompatActivity(),
        IReadFragmentListener,
        IWriteFragmentListener
        , Observer<List<Word>>, TextToSpeech.OnInitListener {

    override fun onInit(p0: Int) {
        Timber.i("TextToSpeech - initialized: $p0")
    }

    companion object {
        // Time to wait before show the next fragment
        const val PAUSE_SCREEN_TIME = 1700L
        // Time to pause before show the right answer
        const val PAUSE_SHOW_ANSWER_TIME = 250L
    }

    private lateinit var viewModel: LearnViewModel
    private val learningWords: ArrayList<Word> = lazy {
        arrayListOf<Word>()
    }.value
    private lateinit var interstitialAd: InterstitialAd
    private lateinit var tts: TextToSpeech
    private val languageToLearn = PreferencesHelper().getLangToLearn()

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
        setContentView(R.layout.activity_learn)
        setSupportActionBar(toolbar)

        // set margin on viewPager to preview other fragments
        learn_viewpager.clipToPadding = false
        learn_viewpager.setPadding(60, 0, 60, 0)
        learn_viewpager.pageMargin = 10

        // back button click
        toolbar.setNavigationOnClickListener {
            this.onBackPressed()
        }

        // setup TextToSpeech
        tts = TextToSpeech(this, this)
        tts.language = Locale.US

        // get viewModel
        viewModel = ViewModelProviders.of(this).get(LearnViewModel::class.java)
        // start loading data
        startExercises()

        // setup ads
        runOnFree{
            interstitialAd = InterstitialAd(this)
            interstitialAd.adUnitId = "ca-app-pub-3940256099942544/1033173712"
            interstitialAd.loadAd(AdRequest.Builder().build())
            interstitialAd.adListener = object : AdListener(){
                override fun onAdClosed() {
                    super.onAdClosed()
                    // close activity on ad closed
                    this@LearningActivity.finish()
                }
            }
        }
    }

    /**
     * This, will load words to learn,
     * and start the exercises.
     */
    private fun startExercises(){
        // get words to learn
        viewModel.getLearningWords()?.observe(this, this)
        // pre-load fake words from database
        viewModel.getFakeWords()
    }

    /**
     * Method to receive words that need to be learned at this time.
     * @param words the words to be learned
     */
    private var exercisesCount = 0
    override fun onChanged(words: List<Word>?) {
        // remove observer, we need to observe just one time
        viewModel.getLearningWords()?.removeObserver(this)

        // clear current list
        learningWords.clear()

        // add new words to learn
        when((exercisesCount % 2) == 0){
            // add into learning list
            true -> learningWords.addAll(words!!.take(4))
            false -> learningWords.addAll(words!!.takeLast(4))
        }

        // increase exercises count
        exercisesCount += 1

        // the user start learning by reading,
        // then by reading reversed, then
        // by writing, and then by writing reversed
        // this function will call the other sequentially.
        showReadingFragments()
    }

    /**
     * Replace fragments adapter with news fragments to how.
     * @param fragments the news fragments to be shown.
     * NOTE: this will remove the holds fragments on UI.
     */
    private fun showFragments(fragments: ArrayList<Fragment>){
        // show fragments
        learn_viewpager.adapter = MyPagerAdapter(supportFragmentManager, fragments)
    }

    /**
     * Show learn by reading fragments.
     * @param reversed if true reverse the translation and fake words.
     */
    private fun showReadingFragments(reversed: Boolean = false){
        Timber.i("showReadingFragments() - reversed: $reversed")

        val fragments = arrayListOf(
                TrainFragment.newInstance(learningWords), // to train the words
                ReadingFragment.newInstance(learningWords [0], reversed),
                ReadingFragment.newInstance(learningWords[1], reversed),
                ReadingFragment.newInstance(learningWords[3], reversed),
                ReadingFragment.newInstance(learningWords[2], reversed, isLastFragment = true)
        )

        showFragments(fragments)
    }

    /**
     * Show learn by writing fragments.
     * @param reversed if true reverse the translation.
     */
    private fun showWritingFragments(reversed: Boolean = false){
        Timber.i("showWritingFragments() - reversed: $reversed")

        val fragments = arrayListOf<Fragment>(
                WritingFragment.newInstance(learningWords [0], reversed),
                WritingFragment.newInstance(learningWords[1], reversed),
                WritingFragment.newInstance(learningWords[3], reversed),
                WritingFragment.newInstance(learningWords[2], reversed, isLastFragment = true)
        )

        showFragments(fragments)
    }

    /**
     * Called when the last learn reading fragment was finished
     *  @param reversed if true, user learned by reading the reversed translation.
     */
    override fun onLearnReadingFinished(reversed: Boolean) {
        Timber.i("onLearnReadingFinished() reversed: $reversed")

        when(reversed){
            true -> showWritingFragments()
            false -> showReadingFragments( true)
        }
    }

    /**
     * Called when the last learn writing fragment was finished
     * @param reversed if true, user learned by writing the reversed translation.
     */
    override fun onLearnWritingFinished(reversed: Boolean) {
        Timber.i("onLearnWritingFinished() reversed: $reversed")

        // if user learned by writing two times, repeat tasks instead of finish the activity
        // else, learn again by writing
        when(reversed){
            true -> {
                startExercises()
                //this.finish()
            }
            false -> showWritingFragments( true)
        }
    }

    override fun onBackPressed() {
        //super.onBackPressed()
        showInterstitialAdsOrCloseActivity()
    }

    private fun showInterstitialAdsOrCloseActivity(){
        if(interstitialAd.isLoaded){
            interstitialAd.show()
        }else{
            this.finish()
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
