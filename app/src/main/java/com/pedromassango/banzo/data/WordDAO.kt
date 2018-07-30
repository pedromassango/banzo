package com.pedromassango.banzo.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.pedromassango.banzo.data.models.Word

/**
 * Created by Pedro Massango on 6/17/18.
 */
@Dao
interface WordDAO {

    companion object {
        const val FAKE_WORDS_LIMIT = 48
        const val DAILY_WORDS_LIMIT = 8
    }

    // get words that is in learning state
    @Query("SELECT * FROM Word WHERE Word.learning = 1")
    fun getLearningWordsList(): List<Word>?

    // get words that  have not been learned
    @Query("SELECT * FROM Word WHERE Word.learned = 0 ORDER BY Random() LIMIT $DAILY_WORDS_LIMIT")
    fun getWordsToLearnList(): List<Word>?

    // get the number of learning and learned words
    @Query("SELECT COUNT(*) FROM Word WHERE( learning = 1 OR learned = 1)")
    fun getLearningAndLearnedWordsCount(): LiveData<Int>

    // get the number of learning words with more fails than hit
    @Query("SELECT COUNT(*) FROM Word WHERE( learning = 1 AND failCount > 2)")
    fun getChallengingWordsCount(): LiveData<Int>


    @Query("SELECT * FROM Word WHERE Word.learning = 1")
    fun getLearningWords(): LiveData<List<Word>>?

    // get words that  have not been learned
    @Query("SELECT * FROM Word WHERE Word.learned = 0 ORDER BY Random() LIMIT $DAILY_WORDS_LIMIT")
    fun getWordsToLearn(): LiveData<List<Word>>?

    // return all words is not current learning
    @Query("SELECT * FROM Word WHERE Word.learning = 0 ORDER BY Random() LIMIT $FAKE_WORDS_LIMIT")
    fun getFakeWords(): LiveData<List<Word>>?

    // get all words that have been learned or learning
    @Query("SELECT * FROM Word WHERE Word.learned = 1 OR Word.learning = 1 OR Word.hitCounter > 1 OR Word.failCount > 1")
    fun getLearnedAndLearningWords(): LiveData<List<Word>>

    /**
     * Return the total of learned words
     * The number of words with more hits, than fails.
     */
    @Query("SELECT COUNT(*) FROM Word WHERE ((Word.hitCounter - Word.failCount) > 3)")
    //@Query("SELECT COUNT(*) FROM Word WHERE Word.failCount = 0 AND Word.hitCounter > 3")
    fun getLearnedWords(): LiveData<Int>

    @Update
    fun update(word: Word)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(word: Word)
}