package com.pedromassango.banzo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.pedromassango.banzo.ui.learned.LearnedFragment
import kotlinx.android.synthetic.main.activity_learned.*

class LearnedActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_learned)
        setSupportActionBar(toolbar)

        // On navigation click, go back.
        toolbar.setNavigationOnClickListener {
            this@LearnedActivity.onBackPressed()
        }

        // show the fragment on first setup
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, LearnedFragment.newInstance())
                    .commitNow()
        }
    }

}
