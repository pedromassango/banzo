package com.pedromassango.banzo.ui.setup

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pedromassango.banzo.R
import com.pedromassango.banzo.data.models.Language
import android.widget.CheckBox
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_lang_to_learn.*
import kotlinx.android.synthetic.main.fragment_lang_to_learn.view.*

/**
 * A fragment to select the language to learn.
 */
class LangToLearnFragment : Fragment(), (Language) -> Unit {

    private lateinit var viewModel: SetupSharedViewModel

    // Available languages to learn
    private val languages = arrayListOf(
            Language("Inglês", "en"),
            Language("Quimbundo", "qb")
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val v = inflater.inflate(R.layout.fragment_lang_to_learn, container, false)

        val adapter = ModelAdapter(languages, this)

        with(v){
            recycler_languages.setHasFixedSize(true)
            recycler_languages.layoutManager = LinearLayoutManager(context)
            recycler_languages.adapter = adapter

            // hide  select button (only showed when an item is clicked)
            btn_select_language.visibility = View.INVISIBLE

            btn_select_language.setOnClickListener {
                // navigate to language level fragment
                it.findNavController().navigate(R.id.action_langToLearnFragment_to_langLevelFragment)
            }
        }
        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(SetupSharedViewModel::class.java)
    }

    /**
     * Called when a language is clicked.
     * @param language selected language
     */
    override fun invoke(language: Language) {
        viewModel.selectLanguage(language)

        btn_select_language.visibility = View.VISIBLE
    }


    inner class ModelAdapter(private var languages: ArrayList<Language>,
                             private val callback: (Language) -> Unit) : RecyclerView.Adapter<ModelAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModelAdapter.ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.row_language, null)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ModelAdapter.ViewHolder, position: Int) {

            val item = languages[position]

            holder.tvTitle.text = item.languageName
            holder.chkSelected.isChecked = item.isSelected

            val listener = { v: View ->

                languages.forEach {
                    it.isSelected = it.languageName == holder.tvTitle.text
                }

                // click listener
                callback( languages[position])

                notifyDataSetChanged()
            }

            holder.tvTitle.setOnClickListener(listener)
            holder.itemView.setOnClickListener(listener)
            holder.chkSelected.setOnClickListener(listener)
        }

        override fun getItemCount(): Int {
            return languages.size
        }

        inner class ViewHolder(itemLayoutView: View) : RecyclerView.ViewHolder(itemLayoutView) {
            var chkSelected: CheckBox = itemLayoutView.findViewById(R.id.cb_selected) as CheckBox
            var tvTitle: TextView = itemLayoutView.findViewById(R.id.tv_title) as TextView
        }
    }

}
