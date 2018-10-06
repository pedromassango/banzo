package com.pedromassango.banzo.ui.main

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.findNavController
import com.pedromassango.banzo.R
import com.pedromassango.banzo.data.WordDAO
import com.pedromassango.banzo.data.preferences.PreferencesHelper
import com.pedromassango.banzo.extras.DateUtils
import com.pedromassango.banzo.services.TimerService
import kotlinx.android.synthetic.main.main_fragment.*
import kotlinx.android.synthetic.main.main_fragment.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainFragment : Fragment() {

    private val mainViewModel: MainViewModel by viewModel()

    private val learningWordsWithMoreHitsCountObserver: Observer<Int> by lazy {
        Observer<Int>{

            // if no at least one word with more hit, do nothing
            if(it == 0) return@Observer

            // true if the user learned at least MIN_HIT_ALLOWED words this day
            val goodLevel = it >= WordDAO.MIN_HIT_ALLOWED

            val anim = when(goodLevel){
                true ->{
                    tv_learn_status?.text = getString(R.string.voce_est_se_saind_muito_bem)
                    R.raw.animation_fast_learn
                }
                false ->{
                    tv_learn_status?.text = getString(R.string.quanto_mais_treinar_mais_palavras_vai_aprender)
                    R.raw.animation_slow_learn
                }
            }

            // play the animation
            lottie_anim_view?.setAnimation(anim)
            lottie_anim_view?.playAnimation()
        }
    }

    // this receiver is notified every break time,
    // break time is when the user should take a pause in learn words.
    private val receiver = object: BroadcastReceiver(){
        // save the pause animation state
        private var started = false
        override fun onReceive(p0: Context?, p1: Intent?) {
            val time = p1!!.getLongExtra(TimerService.KEY_TIME, 0)

            when(time.toInt() != 0){
                true ->{
                    // play if not played yet
                    if(!started) {
                        // remove current observer, we will add it when break time is finished
                        mainViewModel.getLearningWordsWithMoreHitsCount()?.removeObserver(learningWordsWithMoreHitsCountObserver)

                        lottie_anim_view?.setAnimation(R.raw.animation_pause_time)
                        lottie_anim_view?.playAnimation()
                        started = true
                    }

                    // update info text
                    tv_learn_status?.text = getString(R.string.momento_de_intervalo)
                    // updated button text, and remove click listener
                    btn_start_learning?.setOnClickListener(null)
                    btn_start_learning?.text = DateUtils.getMinuteAndSecond(time)
                }
                false ->{
                    started = false

                    // reload learning words views
                    mainViewModel.getLearningWordsWithMoreHitsCount()
                            ?.observe(this@MainFragment, learningWordsWithMoreHitsCountObserver)

                    btn_start_learning?.text = getString(R.string.iniciar)
                    btn_start_learning?.setOnClickListener(btnStartLearningListener)
                }
            }
        }
    }
    // start activity to learn
    private val btnStartLearningListener: View.OnClickListener by lazy {
        View.OnClickListener {
            it.findNavController().navigate(R.id.action_mainFragment_to_learningActivity)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val v = inflater.inflate(R.layout.main_fragment, container, false)

        with(v){
            btn_start_learning.setOnClickListener(btnStartLearningListener)
        }

        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // handle learn speed animation
        mainViewModel.getLearningWordsWithMoreHitsCount()
                ?.observe(activity!!, learningWordsWithMoreHitsCountObserver)
    }

    override fun onResume() {
        super.onResume()
        // Register localBradCast
        LocalBroadcastManager
                .getInstance(context!!)
                .registerReceiver(receiver, IntentFilter(TimerService.TIMER_ACTION))

        // last day tha the user entered on app
        val lastLearnedDay = PreferencesHelper().lastLearnedDay

    }

    override fun onStop() {
        super.onStop()

        LocalBroadcastManager.getInstance(context!!)
                .unregisterReceiver(receiver)
    }
}
