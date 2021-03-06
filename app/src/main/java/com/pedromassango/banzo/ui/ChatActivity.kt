package com.pedromassango.banzo.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.pedromassango.banzo.R
import com.pedromassango.banzo.ui.chat.ChatFragment
import com.pedromassango.banzo.ui.chat.ChatViewModel
import kotlinx.android.synthetic.main.chat_activity.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class ChatActivity : AppCompatActivity() {

    private val chatViewModel: ChatViewModel by viewModel()

    private var clubId = ""
    private var clubName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_activity)

        // toolbar
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { super.onBackPressed() }

        // get intent data
        clubName = intent.getStringExtra(KEY_CLUB_NAME)
        clubId = intent.getStringExtra(KEY_CLUB_ID)

        Timber.i("club name: $clubName")

        // show club name
        toolbar.title = clubName

        // pass data to ViewModel
        // This will cause immediate load comments in ChatFragment
        chatViewModel.setClubId( clubId)

        // show fragment
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, ChatFragment.newInstance())
                    .commitNow()
        }
    }

    companion object {
        const val KEY_CLUB_NAME = "cn"
        const val KEY_CLUB_ID = "ci"
    }
}
