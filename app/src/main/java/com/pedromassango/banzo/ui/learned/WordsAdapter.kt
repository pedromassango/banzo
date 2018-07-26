package com.pedromassango.banzo.ui.learned

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pedromassango.banzo.R
import com.pedromassango.banzo.data.models.Word
import kotlinx.android.synthetic.main.row_word.view.*

/**
 * Created by Pedro Massango on 7/2/18.
 */
class WordViewModel(view: View): RecyclerView.ViewHolder(view){

    fun bind(word: Word){
        with(itemView){
            tv_translation.text = word.translation
            tv_default.text = word.ptWord
        }
    }
}

class WordsAdapter(val fragment: LearnedFragment) : RecyclerView.Adapter<WordViewModel>(){
    private val data = mutableListOf<Word>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewModel {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.row_word, parent, false)
        return (WordViewModel(v))
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: WordViewModel, position: Int) {
        val item = data[position]
        holder.bind( item)
        holder.itemView.setOnClickListener {
            fragment.speak(item.translation)
        }
    }

    @Synchronized
    fun add(words: List<Word>){
        data.addAll(words)
        notifyDataSetChanged()
    }
}