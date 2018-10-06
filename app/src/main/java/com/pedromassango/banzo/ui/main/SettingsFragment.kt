package com.pedromassango.banzo.ui.main


import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.pedromassango.banzo.BuildConfig

import com.pedromassango.banzo.R
import com.pedromassango.banzo.data.AuthManager
import com.pedromassango.banzo.data.preferences.PreferencesHelper
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import kotlin.math.log

/**
 * A simple [Fragment] subclass.
 *
 */
class SettingsFragment : PreferenceFragmentCompat() {

    private val mainViewModel: MainViewModel by viewModel()

    // preferences helper
    private val preferencesHelper: PreferencesHelper by inject()

    // authentication manager
    private val authManager: AuthManager by inject()

    // UI preferences
    private val usernamePrefs: Preference by lazy{
        findPreference(getString(R.string.prefs_username))
    }
    private val removeAccountPrefs: Preference by lazy{
        findPreference(getString(R.string.prefs_remove_account))
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.settings_screen)

        val maxWords = findPreference( getString(R.string.prefs_max_daily_words))
        maxWords.isEnabled = !BuildConfig.DEBUG
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setBackgroundColor(ResourcesCompat.getColor(resources, android.R.color.transparent, null))
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mainViewModel.getAuthState().observe(this, Observer{isLoggedIn ->
            // only enable logout button, if user is logged in
            removeAccountPrefs.isEnabled = isLoggedIn
            // show/hide username
            usernamePrefs.summary = when(isLoggedIn){
                true -> preferencesHelper.username
                false -> ""
            }
        })
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {

        when (preference!!.key) {
            getString(R.string.prefs_learned_words) ->{
                // Navigate to Learned Words Activity
                view?.findNavController()?.navigate(R.id.action_settingsFragment_to_learnedActivity)
            }
            getString(R.string.prefs_rate_app) -> startPlaystoreAppPage(context!!)
            getString(R.string.prefs_share_app) -> startShareApp(activity!!)
            getString(R.string.prefs_remove_account) -> authManager.logout()
        }

        return super.onPreferenceTreeClick(preference)
    }

    private fun startShareApp(activity: Activity) {
        try {
            val packageName = activity.packageName
            val packageInfo = activity.packageManager.getPackageInfo(packageName, 0)
            val dir = packageInfo.applicationInfo.sourceDir
            val tempFile = File(dir)

            val i = Intent()
            i.action = Intent.ACTION_SEND
            i.setDataAndType(Uri.fromFile(tempFile), "application/vnd.android.package-archive")
            i.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(tempFile))

            // Testing...
            //Only use bluetooth to share the app
            i.`package` = "com.android.bluetooth"
            activity.startActivity(i)
        } catch (e: Exception) {
            // Share app error
            e.printStackTrace()
        }

    }


    private fun startPlaystoreAppPage(context: Context) {
        val uri = Uri.parse("market://details?id=" + context.packageName)
        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
        } else {
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK)

        }
        try {
            context.startActivity(goToMarket)
        } catch (e: ActivityNotFoundException) {
            context.startActivity(Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + context.packageName)))
        }

    }

}
