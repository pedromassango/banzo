package com.pedromassango.banzo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavHost
import androidx.navigation.Navigation
import androidx.navigation.findNavController

class SetupActivity : AppCompatActivity(), NavHost {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup)
    }

    override fun onSupportNavigateUp() =
            findNavController(R.id.setup_nav_host_fragment).navigateUp()

    override fun getNavController(): NavController {
        return Navigation.findNavController(this, R.id.setup_nav_host_fragment)
    }
}
