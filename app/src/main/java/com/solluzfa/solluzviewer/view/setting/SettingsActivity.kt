package com.solluzfa.solluzviewer.view.setting

import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.preference.*
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.solluzfa.solluzviewer.Log
import com.solluzfa.solluzviewer.R
import com.solluzfa.solluzviewer.controls.SolluzService
import com.solluzfa.solluzviewer.utils.InjectorUtils
import com.solluzfa.solluzviewer.utils.InjectorUtils.EXTRA_KEY_MACHINE_ID
import com.solluzfa.solluzviewer.view.MainActivity

class SettingsActivity : AppCompatPreferenceActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupActionBar()
        machineID = intent.getIntExtra(EXTRA_KEY_MACHINE_ID, 99)
        fragmentManager.beginTransaction()
            .replace(android.R.id.content, GeneralPreferenceFragment()).commit()
    }

    override fun onPause() {
        super.onPause()
        val intent = Intent(this, SolluzService::class.java).also {
            it.action = InjectorUtils.UPDATE_SETTINGS
        }
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

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    class GeneralPreferenceFragment : PreferenceFragment() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.pref_general)
            setHasOptionsMenu(true)
            setValueValue()
        }

        private fun setValueValue() {
            putPreference(machineID.toString(), "")

            bindPreferenceSummaryToValue(findPreference(resources.getString(R.string.pref_key_url_text)))
            bindPreferenceSummaryToValue(findPreference(resources.getString(R.string.pref_key_company_code_text)))
            bindPreferenceSummaryToValue(findPreference(resources.getString(R.string.pref_key_interval_list)))
        }

        override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
            super.onCreateOptionsMenu(menu, inflater)
            inflater?.inflate(R.menu.setting_menu, menu)
        }

        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            val id = item.itemId
            return if (id == R.id.save) {
                putPreference("", machineID.toString())
                startActivity(Intent(activity, MainActivity::class.java))
                true
            } else if (id == android.R.id.home) {
                startActivity(Intent(activity, MainActivity::class.java))
                true
            } else super.onOptionsItemSelected(item)
        }

        private fun putPreference(from: String, to: String) {
            Log.i(TAG, "putPreference from: $from, to: $to")

            val uriTextKey = resources.getString(R.string.pref_key_url_text)
            val companyCodeKey = resources.getString(R.string.pref_key_company_code_text)
            val intervalKey = resources.getString(R.string.pref_key_interval_list)
            val pushKey = resources.getString(R.string.pref_key_push_switch)

            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            sharedPreferences.edit().apply {
                putString(
                    uriTextKey + to,
                    sharedPreferences.getString(
                        uriTextKey + from,
                        resources.getString(R.string.pref_default_url_address)
                    )
                )
                putString(
                    companyCodeKey + to,
                    sharedPreferences.getString(
                        companyCodeKey + from,
                        resources.getString(R.string.pref_default_company_code)
                    )
                )
                putString(
                    intervalKey + to,
                    sharedPreferences.getString(
                        intervalKey + from,
                        resources.getString(R.string.pref_default_reading_interval)
                    )
                )
                putBoolean(
                    pushKey + to,
                    sharedPreferences.getBoolean(
                        pushKey + from, true
                    )
                )
                commit()
            }
        }
    }

    companion object {
        private const val TAG: String = "SettingsActivity"
        var machineID = 0

        private val sBindPreferenceSummaryToValueListener =
            Preference.OnPreferenceChangeListener { preference, value ->
                val stringValue = value.toString()

                if (preference is ListPreference) {
                    val listPreference = preference
                    val index = listPreference.findIndexOfValue(stringValue)
                    preference.setSummary(
                        if (index >= 0)
                            listPreference.entries[index]
                        else
                            null
                    )

                } else {
                    preference.summary = stringValue
                }
                true
            }

        private fun isXLargeTablet(context: Context): Boolean {
            return context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_XLARGE
        }

        private fun bindPreferenceSummaryToValue(preference: Preference) {
            preference.onPreferenceChangeListener = sBindPreferenceSummaryToValueListener

            sBindPreferenceSummaryToValueListener.onPreferenceChange(
                preference,
                PreferenceManager
                    .getDefaultSharedPreferences(preference.context)
                    .getString(preference.key, "")
            )
        }
    }
}
