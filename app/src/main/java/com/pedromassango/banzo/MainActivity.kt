package com.pedromassango.banzo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.navigation.NavController
import androidx.navigation.NavHost
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), NavHost {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Setup bottom navigation view
        bottom_navigation_view.selectedItemId = bottom_navigation_view.menu.get(1).itemId
        bottom_navigation_view.setOnNavigationItemSelectedListener { menuItem ->

            // Navigate between fragments from main nat path
            Navigation.findNavController(this@MainActivity, R.id.main_nav_host_fragment)
                    .navigate(menuItem.itemId)

            return@setOnNavigationItemSelectedListener true
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.main_nav_host_fragment).navigateUp()
    }

    override fun getNavController(): NavController {
        return Navigation.findNavController(this, R.id.main_nav_host_fragment)
    }
}
