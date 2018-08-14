package com.pedromassango.banzo.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.pedromassango.banzo.ui.LearningActivity
import com.pedromassango.banzo.R
import com.pedromassango.banzo.data.preferences.PreferencesHelper
import com.pedromassango.banzo.extras.ActivityUtils
import com.pedromassango.banzo.extras.DateUtils
import kotlinx.android.synthetic.main.main_fragment.*
import kotlinx.android.synthetic.main.main_fragment.view.*

class MainFragment : Fragment() {

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val v = inflater.inflate(R.layout.main_fragment, container, false)

        with(v){
            btn_start_learning.setOnClickListener {
                // start activity to learn
                ActivityUtils.start(context, LearningActivity::class.java)
            }
        }

        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
    }

    override fun onResume() {
        super.onResume()

        // last day tha the user entered on app
        val lastLearnedDay = PreferencesHelper().lastLearnedDay

        // show info depending of last day value
        tv_learn_status.text = when(lastLearnedDay){
            0 -> getString(R.string.muito_bem_vamos_come_ar_com_o_treinamento_de_hoje)
            DateUtils.currentDay() -> getString(R.string.voce_tem_quatro_palavras_para_revisar)
            else -> getString(R.string.voce_tem_palavras_novas_para_aprender)
        }
    }

}
