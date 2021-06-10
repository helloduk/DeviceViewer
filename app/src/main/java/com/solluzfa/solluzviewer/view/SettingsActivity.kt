package com.solluzfa.solluzviewer.view

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.preference.*
import android.support.annotation.RequiresApi
import android.view.MenuItem
import com.solluzfa.solluzviewer.R
import com.solluzfa.solluzviewer.controls.SolluzService
import com.solluzfa.solluzviewer.utils.InjectorUtils

@RequiresApi(Build.VERSION_CODES.M)
class SettingsActivity : AppCompatPreferenceActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupActionBar()
        fragmentManager.beginTransaction().replace(android.R.id.content, GeneralPreferenceFragment()).commit()
    }

    override fun onPause() {
        super.onPause()
        val intent = Intent(this, SolluzService::class.java).also { it.action = InjectorUtils.UPDATE_SETTINGS }
        startService(intent)
    }

    private fun setupActionBar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onIsMultiPane(): Boolean {
        return isXLargeTablet(this)
    }

    override fun isValidFragment(fragmentName: String): Boolean {
        return PreferenceFragment::class.java.name == fragmentName
                || GeneralPreferenceFragment::class.java.name == fragmentName
    }

    @RequiresApi(Build.VERSION_CODES.M)
    class GeneralPreferenceFragment : PreferenceFragment() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.pref_general)
            setHasOptionsMenu(true)

            registerPreferenceListener()

            updateEnabledAboutBluetooth()
        }

        private fun updateEnabledAboutBluetooth() {
            val bluetoothPreference = findPreference(context.getString(R.string.pref_key_bluetooth))

            if (context.packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                enableExceptBluetooth(!(bluetoothPreference as SwitchPreference).isChecked)
            } else {
                bluetoothPreference.isEnabled = false
                bluetoothPreference.summary = getString(R.string.pref_description_not_support_bluetooth)
            }
        }

        private fun registerPreferenceListener() {
            bindPreferenceSummaryToValue(findPreference(context.getString(R.string.pref_key_url_text)))
            bindPreferenceSummaryToValue(findPreference(context.getString(R.string.pref_key_company_code_text)))
            bindPreferenceSummaryToValue(findPreference(context.getString(R.string.pref_key_interval_list)))
            findPreference(context.getString(R.string.pref_key_bluetooth))
                    .onPreferenceChangeListener = bindPreferenceSummaryToValueListener
        }

        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            val id = item.itemId
            if (id == android.R.id.home) {
                startActivity(Intent(activity, MainActivity::class.java))
                return true
            }
            return super.onOptionsItemSelected(item)
        }

        private fun bindPreferenceSummaryToValue(preference: Preference) {
            preference.onPreferenceChangeListener = bindPreferenceSummaryToValueListener

            // Trigger the listener immediately with the preference's
            // current value.
            bindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.context)
                            .getString(preference.key, ""))
        }

        private val bindPreferenceSummaryToValueListener = Preference.OnPreferenceChangeListener { preference, value ->
            val stringValue = value.toString()

            if (preference is ListPreference) { // Interval
                val index = preference.findIndexOfValue(stringValue)
                preference.setSummary(
                        if (index >= 0)
                            preference.entries[index]
                        else
                            null)

            } else if (preference.key == context.getString(R.string.pref_key_bluetooth)) {
                enableExceptBluetooth(!(value as Boolean))
            } else {
                preference.summary = stringValue
            }

            true
        }

        private fun enableExceptBluetooth(enable: Boolean) {
            findPreference(context.getString(R.string.pref_key_url_text)).isEnabled = enable
            findPreference(context.getString(R.string.pref_key_company_code_text)).isEnabled = enable
            findPreference(context.getString(R.string.pref_key_interval_list)).isEnabled = enable
        }
    }


    companion object {
        private fun isXLargeTablet(context: Context): Boolean {
            return context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_XLARGE
        }
    }
}
