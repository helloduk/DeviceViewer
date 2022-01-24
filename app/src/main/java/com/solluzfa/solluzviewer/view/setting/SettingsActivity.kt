package com.solluzfa.solluzviewer.view.setting

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.AlertDialog
import android.app.Dialog
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
import java.util.*
import kotlin.concurrent.schedule

class SettingsActivity : AppCompatPreferenceActivity() {
    var dialog: Dialog? = null

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupActionBar()
        machineID = intent.getIntExtra(EXTRA_KEY_MACHINE_ID, 99)
        fragmentManager.beginTransaction()
            .replace(android.R.id.content, GeneralPreferenceFragment()).commit()
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setView(R.layout.progress)
        dialog = builder.create()
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
            putPreference(machineID.toString(), "", true)

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
                val intent = Intent(activity, SolluzService::class.java).also {
                    it.action = InjectorUtils.UPDATE_SETTINGS
                }
                activity?.startService(intent)

                if (activity is SettingsActivity) {
                    (activity as SettingsActivity).dialog?.show()
                    Timer().schedule(1000) {
                        (activity as SettingsActivity).dialog?.dismiss()
                        activity?.finish()
                    }
                }
                true
            } else if (id == android.R.id.home) {
                activity?.finish()
                true
            } else super.onOptionsItemSelected(item)
        }



        @SuppressLint("NewApi", "ApplySharedPref")
        private fun putPreference(from: String, to: String, setText: Boolean = false) {
            Log.i(TAG, "putPreference from: $from, to: $to")

            val uriTextKey = resources.getString(R.string.pref_key_url_text)
            val companyCodeKey = resources.getString(R.string.pref_key_company_code_text)
            val intervalKey = resources.getString(R.string.pref_key_interval_list)
            val pushKey = resources.getString(R.string.pref_key_push_switch)

            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            sharedPreferences.edit().apply {
                val uriValue = sharedPreferences.getString(
                    uriTextKey + from,
                    resources.getString(R.string.pref_default_url_address)
                )
                putString(uriTextKey + to, uriValue)

                val companyCodeValue = sharedPreferences.getString(
                    companyCodeKey + from,
                    resources.getString(R.string.pref_default_company_code)
                )
                putString(companyCodeKey + to, companyCodeValue)

                val intervalValue = sharedPreferences.getString(
                    intervalKey + from,
                    resources.getString(R.string.pref_default_reading_interval)
                )
                putString(intervalKey + to, intervalValue)

                val pushValue = sharedPreferences.getBoolean(pushKey + from, true)
                putBoolean(pushKey + to, pushValue)

                commit()

                if (setText) {
                    (findPreference(uriTextKey) as EditTextPreference).text = uriValue
                    (findPreference(companyCodeKey) as EditTextPreference).text = companyCodeValue
                    (findPreference(intervalKey) as ListPreference).value = intervalValue
                    (findPreference(pushKey) as SwitchPreference).isChecked = pushValue
                }
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
                    val index = preference.findIndexOfValue(stringValue)
                    preference.setSummary(
                        if (index >= 0) preference.entries[index]
                        else null
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
