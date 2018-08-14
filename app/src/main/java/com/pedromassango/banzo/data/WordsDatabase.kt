package com.pedromassango.banzo.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.pedromassango.banzo.MainApplication
import com.pedromassango.banzo.data.models.Word

/**
 * Created by Pedro Massango on 6/24/18.
 */
@Database(entities = [(Word::class)], version = 2)
abstract class WordsDatabase : RoomDatabase() {
    abstract val wordDAO: WordDAO

    companion object {
        private var INSTANCE: WordsDatabase? = null

        fun getInstance(context: Context = MainApplication.applicationContext()): WordsDatabase {
            if (INSTANCE == null) {
                INSTANCE = create(context)
            }

            return INSTANCE!!
        }

        // Create the database
        private fun create(context: Context): WordsDatabase {
            val builder = Room.databaseBuilder(context.applicationContext,
                    WordsDatabase::class.java,
                    "tasks")
            // TODO: allow room in UI thread
                    .allowMainThreadQueries()

            return builder.build()
        }
    }
}