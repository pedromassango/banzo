package com.pedromassango.banzo.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.pedromassango.banzo.ui.LearningActivity
import com.pedromassango.banzo.R
import com.pedromassango.banzo.data.preferences.PreferencesHelper
import com.pedromassango.banzo.extras.ActivityUtils
import com.pedromassango.banzo.extras.DateUtils
import kotlinx.android.synthetic.main.clubs_fragment.*
import kotlinx.android.synthetic.main.main_fragment.*
import kotlinx.android.synthetic.main.main_fragment.view.*

class ClubsFragment : Fragment() {

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val v = inflater.inflate(R.layout.clubs_fragment, container, false)

        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        // remove no logged view, if the user is logged
        viewModel.getAuthState().observe(this, Observer { isLoggedIn ->
            when (isLoggedIn) {
                true -> {
                    no_logged_view.visibility = View.GONE
                    logged_view.visibility = View.VISIBLE
                }
                false -> {
                    no_logged_view.visibility = View.VISIBLE
                    logged_view.visibility = View.GONE
                }
            }
        })

        //TODO: do google signin
    }

}
