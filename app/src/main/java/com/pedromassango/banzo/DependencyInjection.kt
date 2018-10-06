package com.pedromassango.banzo

import com.pedromassango.banzo.data.AppDatabase
import com.pedromassango.banzo.data.AuthManager
import com.pedromassango.banzo.data.CommentsRepository
import com.pedromassango.banzo.data.CommentsService
import com.pedromassango.banzo.data.preferences.PreferencesHelper
import com.pedromassango.banzo.ui.chat.ChatViewModel
import com.pedromassango.banzo.ui.learn.LearnViewModel
import com.pedromassango.banzo.ui.learned.LearnedViewModel
import com.pedromassango.banzo.ui.main.MainViewModel
import com.pedromassango.banzo.ui.setup.SetupSharedViewModel
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

object DependencyInjection {

    val appModule = module {
        single { AppDatabase.getInstance( get()) }
        single { (get() as AppDatabase).wordDAO }
        factory { PreferencesHelper( get()) }
        single { AuthManager( get()) }
        single{ CommentsRepository( get(), get()) }
        single { CommentsService() }

        viewModel { SetupSharedViewModel(get()) }
        viewModel { ChatViewModel( get(), get()) }
        viewModel { LearnViewModel( get()) }
        viewModel { LearnedViewModel( get()) }
        viewModel { MainViewModel( get(), get()) }
    }
}