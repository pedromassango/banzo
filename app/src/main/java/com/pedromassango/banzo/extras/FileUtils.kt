package com.pedromassango.banzo.extras

import android.content.res.AssetManager
import timber.log.Timber
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

/**
 * Created by Pedro Massango on 6/24/18.
 */
class FileUtils(private val assetsManager: AssetManager) {

    companion object {
        const val LANGUAGE_PORTUGUESE = 0
        const val LANGUAGE_ENGLISH = 1
    }

    fun read(type: Int): ArrayList<String> {
        var reader: BufferedReader? = null
        val result = arrayListOf<String>()

        val fileName = when(type){
            LANGUAGE_ENGLISH -> "en_words.txt"
            else -> "pt_words.txt"
        }

        try {
            reader = BufferedReader(InputStreamReader(assetsManager.open(fileName)))

            // do reading, usually loop until end of file reading
            var line: String? = null
            while ({ line = reader.readLine(); line }() != null) {

                //process line
                Timber.i("Data obtained: $line")
                // set word in temp list
                result.add(line!!)
            }

        } catch (e: IOException) {
            //log the exception
            Timber.e("Read file exception")
            Timber.e(e)
        } finally {
            if (reader != null) {
                try {
                    reader.close()
                } catch (e: IOException) {
                    //log the exception
                    Timber.e(e)
                }
            }
        }

        return result
    }
}