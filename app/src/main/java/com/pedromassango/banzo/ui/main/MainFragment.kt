package com.pedromassango.banzo.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.pedromassango.banzo.ui.LearningActivity
import androidx.navigation.findNavController
import com.pedromassango.banzo.R
import com.pedromassango.banzo.data.WordDAO
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
                it.findNavController().navigate(R.id.action_mainFragment_to_learningActivity)
            }
        }

        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        // handle learn speed animation
        viewModel.getLearningWordsWithMoreHitsCount()?.observe(activity!!, Observer{

            // if no at least one word with more hit, do nothing
            if(it == 0) return@Observer

            // true if the user learned at least MIN_HIT_ALLOWED words this day
            val goodLevel = it >= WordDAO.MIN_HIT_ALLOWED

            val anim = when(goodLevel){
                true ->{
                    tv_learn_status.text = getString(R.string.voce_est_se_saind_muito_bem)
                    R.raw.animation_fast_learn
                }
                false ->{
                    tv_learn_status.text = getString(R.string.quanto_mais_treinar_mais_palavras_vai_aprender)
                    R.raw.animation_slow_learn
                }
            }

            // play the animation
            lottie_anim_view?.setAnimation(anim)
            lottie_anim_view?.playAnimation()
        })
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
