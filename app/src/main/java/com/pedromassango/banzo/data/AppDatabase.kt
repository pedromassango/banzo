package com.pedromassango.banzo.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.pedromassango.banzo.MainApplication
import com.pedromassango.banzo.data.models.Comment
import com.pedromassango.banzo.data.models.Word

/**
 * Created by Pedro Massango on 6/24/18.
 */
@Database(entities = [Word::class, Comment::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    // words DAO
    abstract val wordDAO: WordDAO
    // comments DAO
    abstract val commentsDao: CommentDao

    companion object {
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context = MainApplication.applicationContext()): AppDatabase {
            if (INSTANCE == null) {
                INSTANCE = create(context)
            }

            return INSTANCE!!
        }

        // Create the database
        private fun create(context: Context): AppDatabase {
            val builder = Room.databaseBuilder(context.applicationContext,
                    AppDatabase::class.java,
                    "banzo_app_database")
            // TODO: allow room in UI thread
                    .allowMainThreadQueries()

            return builder.build()
        }
    }
}