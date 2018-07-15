package com.pedromassango.banzo.ui.setup


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pedromassango.banzo.MainActivity

import com.pedromassango.banzo.R
import com.pedromassango.banzo.data.models.Language
import com.pedromassango.banzo.data.models.Level
import com.pedromassango.banzo.data.models.Word
import com.pedromassango.banzo.data.preferences.PreferencesHelper
import com.pedromassango.banzo.enums.LanguageLevels
import com.pedromassango.banzo.enums.LanguagestTypes
import com.pedromassango.banzo.extras.ActivityUtils
import com.pedromassango.banzo.extras.FileUtils
import com.pedromassango.banzo.services.DateChangedReceiver
import kotlinx.android.synthetic.main.fragment_lang_level.*
import kotlinx.android.synthetic.main.fragment_lang_level.view.*
import timber.log.Timber


/**
 * A fragment to select the current level experience of the desired language.
 *
 */
class LangLevelFragment : Fragment(), (Level) -> Unit {

    private lateinit var viewModel: SetupSharedViewModel

    private var levels = arrayListOf(
            Level("Iniciante", LanguageLevels.BEGINNER),
            Level("Basico", LanguageLevels.BASIC),
            Level("Intermediario", LanguageLevels.INTERMEDIATE),
            Level("Avancado", LanguageLevels.ADVANCED)
    )

    private lateinit var selectedLanguage: Language
    private lateinit var selectedLevel: Level

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_lang_level, container, false)

        val adapter = LevelsAdapter(levels, this)

        with(v) {
            recycler_levels.setHasFixedSize(true)
            recycler_levels.layoutManager = LinearLayoutManager(context)
            recycler_levels.adapter = adapter

            // hide  select button (only showed when an item is clicked)
            btn_select_level.visibility = View.INVISIBLE

            btn_select_level.setOnClickListener {

                // Save data in database
                saveAppData()
            }
        }

        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(SetupSharedViewModel::class.java)
        viewModel.getSelectedLanguage()
                .observe(this, Observer { selectedLanguage ->

                    checkNotNull(selectedLanguage)
                    this.selectedLanguage = selectedLanguage!!

                    // Append previous selected language to title text
                    tv_lang_level_title.text = String.format(
                            getString(R.string.escolha_seu_nivel_de_1_s),
                            selectedLanguage.languageName
                    )
                })
    }

    private fun saveAppData() {
        // remove button and show progressBar
        btn_select_level.visibility = View.GONE
        progress_save_data.visibility = View.VISIBLE

        // copy all words from assets to database
        // and set first time flat to false
        Thread {

            val fileUtils = FileUtils(resources.assets)
            val portugueseWords = fileUtils.read(LanguagestTypes.PORTUGUES)
            val translationhWords = fileUtils.read(selectedLanguage.type)

            // get all words from assets, and store in database
            portugueseWords.forEachIndexed { index, ptWord ->
                val translationWord = translationhWords[index]

                // Word model class to save in database
                val word = Word(
                        ptWord = ptWord,
                        translation = translationWord
                )

                // save word in database
                viewModel.saveWord(word)
            }

            // Done
            Timber.i("DONE: Save words from assests to database")

            // set first time flat to false
            PreferencesHelper().isFirstTime = false
            // save total words
            PreferencesHelper().totalWordsToLearn = portugueseWords.size

            Timber.i("Total words to learn: ${PreferencesHelper().totalWordsToLearn}")

            Timber.i("starting broadcast to generate words...")
            // start broadcast to setup new words to learn
            val i = Intent(context, DateChangedReceiver::class.java)
            activity?.sendBroadcast(i)

            // navigate to MainActivity
            Timber.i("starting main activity...")
            ActivityUtils.start(context, MainActivity::class.java)

        }.start()
    }

    /**
     * Called when a level is clicked
     * @param level the selected level
     */
    override fun invoke(level: Level) {
        viewModel.selectLevel(level)
        selectedLevel = level

        btn_select_level.visibility = View.VISIBLE
    }


    inner class LevelsAdapter(private var levels: ArrayList<Level>,
                              private val callback: (Level) -> Unit) : RecyclerView.Adapter<LevelsAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LevelsAdapter.ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.row_language, null)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: LevelsAdapter.ViewHolder, position: Int) {

            val item = levels[position]

            holder.tvTitle.text = item.level
            holder.chkSelected.isChecked = item.isSelected

            val listener = { v: View ->

                levels.forEach {
                    it.isSelected = it.level == holder.tvTitle.text
                }

                // click listener
                callback(levels[position])

                notifyDataSetChanged()
            }

            holder.tvTitle.setOnClickListener(listener)
            holder.itemView.setOnClickListener(listener)
            holder.chkSelected.setOnClickListener(listener)
        }

        override fun getItemCount(): Int {
            return levels.size
        }

        inner class ViewHolder(itemLayoutView: View) : RecyclerView.ViewHolder(itemLayoutView) {
            var chkSelected: CheckBox = itemLayoutView.findViewById(R.id.cb_selected) as CheckBox
            var tvTitle: TextView = itemLayoutView.findViewById(R.id.tv_title) as TextView
        }
    }


}
