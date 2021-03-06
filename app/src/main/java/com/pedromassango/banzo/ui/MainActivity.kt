package com.pedromassango.banzo.ui

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.navigation.NavController
import androidx.navigation.NavHost
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.pedromassango.banzo.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), NavHost{

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Start background animation
        val animatedDrawable = container.background as AnimationDrawable
        animatedDrawable.setExitFadeDuration(4000)
        animatedDrawable.setEnterFadeDuration(2000)
        animatedDrawable.start()

        // Setup bottom navigation view
        bottom_navigation_view.setOnNavigationItemSelectedListener { menuItem ->

            // Navigate between fragments from main nat path
            Navigation.findNavController(this@MainActivity, R.id.main_nav_host_fragment)
                    .navigate(menuItem.itemId)

            return@setOnNavigationItemSelectedListener true
        }

        NavigationUI.setupWithNavController(bottom_navigation_view, navController)

        bottom_navigation_view.selectedItemId = bottom_navigation_view.menu[1].itemId
    }

    override fun onSupportNavigateUp() =
            findNavController(R.id.main_nav_host_fragment).navigateUp()

    override fun getNavController() =
            Navigation.findNavController(this, R.id.main_nav_host_fragment)

}
