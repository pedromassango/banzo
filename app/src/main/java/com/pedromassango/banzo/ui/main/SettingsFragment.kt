package com.pedromassango.banzo.ui.main


import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.pedromassango.banzo.BuildConfig
import com.pedromassango.banzo.ui.LearnedActivity

import com.pedromassango.banzo.R
import com.pedromassango.banzo.data.preferences.PreferencesHelper
import com.pedromassango.banzo.extras.ActivityUtils
import java.io.File

/**
 * A simple [Fragment] subclass.
 *
 */
class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.settings_screen)

        // show username
        val username = PreferencesHelper().username
        findPreference(getString(R.string.prefs_username)).summary = username


        val maxWords = findPreference( getString(R.string.prefs_max_daily_words))
        maxWords.isEnabled = !BuildConfig.DEBUG
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.white, null))

    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {

        when (preference!!.key) {
            getString(R.string.prefs_learned_words) ->{
                // Navigate to Learned Words Activity
                view?.findNavController()?.navigate(R.id.action_settingsFragment_to_learnedActivity)
            }
            getString(R.string.prefs_rate_app) -> startPlaystoreAppPage(context!!)
            getString(R.string.prefs_share_app) -> startShareApp(activity!!)
            getString(R.string.prefs_remove_account) -> {
                // TODO:("remove google account (sign in out)"
            }
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
