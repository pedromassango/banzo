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
        /**
         * The minimum of hits acceptable to mark an word as a learned word.
         * NOTE: This is also the number of tests for each word.
         **/
        const val MIN_HIT_ALLOWED = 4
    }

    /**
     * Get words that is in learning state as a List
     */
    @Query("SELECT * FROM Word WHERE Word.learning = 1")
    fun getLearningWordsList(): List<Word>?

    /**
     * Get words that is in learning state as LiveData
     */
    @Query("SELECT * FROM Word WHERE Word.learning = 1")
    fun getLearningWords(): LiveData<List<Word>>?

    /**
     * Return the number of learning and learned words
     */
    @Query("SELECT COUNT(*) FROM Word WHERE( learning = 1 OR learned = 1)")
    fun getLearningAndLearnedWordsCount(): LiveData<Int>

    /**
     * TODO: updated challenging words Query
     * Return the number of learned words with many fails than hits
     */
    @Query("SELECT COUNT(*) FROM Word WHERE((Word.failCount - Word.hitCounter) >= $MIN_HIT_ALLOWED)")
    fun getChallengingWordsCount(): LiveData<Int>

    /**
     * Get words that have not been learned
     */
    @Query("SELECT * FROM Word WHERE Word.learned = 0 ORDER BY Random() LIMIT $DAILY_WORDS_LIMIT")
    fun getWordsToLearn(): LiveData<List<Word>>?

    /**
     * Get words that have not been learned as List
     */
    @Query("SELECT * FROM Word WHERE Word.learned = 0 ORDER BY Random() LIMIT $DAILY_WORDS_LIMIT")
    fun getWordsToLearnList(): List<Word>?

    /**
     * Return all words that is not currently in learning or learned state
     */
    @Query("SELECT * FROM Word WHERE Word.learning = 0 ORDER BY Random() LIMIT $FAKE_WORDS_LIMIT")
    fun getFakeWords(): LiveData<List<Word>>?

    /**
     * Get all words that have been learned or learning
     */
    @Query("SELECT * FROM Word WHERE Word.learned = 1 OR Word.learning = 1 OR Word.hitCounter > 1 OR Word.failCount > 1")
    fun getLearnedAndLearningWords(): LiveData<List<Word>>

    /**
     * Return the total of learned words
     * The number of words with more hits, than fails.
     */
    @Query("SELECT COUNT(*) FROM Word WHERE ((Word.hitCounter - Word.failCount) >= $MIN_HIT_ALLOWED)")
    //@Query("SELECT COUNT(*) FROM Word WHERE Word.failCount = 0 AND Word.hitCounter > 3")
    fun getLearnedWords(): LiveData<Int>

    /**
     * Update an word in database
     */
    @Update
    fun update(word: Word)

    /**
     * Insert an Word in database
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(word: Word)
}