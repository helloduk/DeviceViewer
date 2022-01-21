package com.solluzfa.solluzviewer.view.setting

import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.preference.*
import android.view.MenuItem
import com.solluzfa.solluzviewer.Log
import com.solluzfa.solluzviewer.R
import com.solluzfa.solluzviewer.controls.SolluzService
import com.solluzfa.solluzviewer.utils.InjectorUtils
import com.solluzfa.solluzviewer.utils.InjectorUtils.EXTRA_KEY_MACHINE_ID
import com.solluzfa.solluzviewer.view.MainActivity

/**
 * A [PreferenceActivity] that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 *
 * See [Android Design: Settings](http://developer.android.com/design/patterns/settings.html)
 * for design guidelines and the [Settings API Guide](http://developer.android.com/guide/topics/ui/settings.html)
 * for more information on developing a Settings UI.
 */
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

    /**
     * Set up the [android.app.ActionBar], if the API is available.
     */
    private fun setupActionBar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    /**
     * {@inheritDoc}
     */
    override fun onIsMultiPane(): Boolean {
        return isXLargeTablet(this)
    }

    /**
     * {@inheritDoc}
     */
//    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
//    override fun onBuildHeaders(target: List<PreferenceActivity.Header>) {
//        loadHeadersFromResource(R.xml.pref_headers, target)
//    }


    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    override fun isValidFragment(fragmentName: String): Boolean {
        return PreferenceFragment::class.java.name == fragmentName
                || GeneralPreferenceFragment::class.java.name == fragmentName
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    class GeneralPreferenceFragment : PreferenceFragment() {
        private val TAG: String = "SettingsActivity"

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.pref_general)
            setHasOptionsMenu(true)
            setKeyAndDefaultValue(activity)
        }

        private fun setKeyAndDefaultValue(context: Context) {
            val uriPreference = findPreference(resources.getString(R.string.pref_key_url_text))
            val companyCodePreference = findPreference(resources.getString(R.string.pref_key_company_code_text))
            val intervalListPreference = findPreference(resources.getString(R.string.pref_key_interval_list))
            val pushSwitchPreference = findPreference(resources.getString(R.string.pref_key_push_switch))

            uriPreference.key = uriPreference.key + machineID
            companyCodePreference.key = companyCodePreference.key + machineID
            intervalListPreference.key = intervalListPreference.key + machineID
            pushSwitchPreference.key = pushSwitchPreference.key + machineID

            uriPreference.setDefaultValue(PreferenceManager
                .getDefaultSharedPreferences(context)
                .getString(uriPreference.key, context.resources.getString(R.string.pref_default_url_address))
            )

            companyCodePreference.setDefaultValue(PreferenceManager
                .getDefaultSharedPreferences(context)
                .getString(companyCodePreference.key, context.resources.getString(R.string.pref_default_company_code))
            )

            intervalListPreference.setDefaultValue(PreferenceManager
                .getDefaultSharedPreferences(context)
                .getString(intervalListPreference.key, context.resources.getString(R.string.pref_default_reading_interval))
            )

            pushSwitchPreference.setDefaultValue(PreferenceManager
                .getDefaultSharedPreferences(context)
                .getBoolean(pushSwitchPreference.key, true))

            bindPreferenceSummaryToValue(uriPreference)
            bindPreferenceSummaryToValue(companyCodePreference)
            bindPreferenceSummaryToValue(intervalListPreference)
            bindPreferenceSummaryToValue(pushSwitchPreference)

            Log.i(TAG, "machineID: $machineID" +
                    ", uriPreference.key: ${uriPreference.key}" +
                    ", companyCodePreference.key: ${companyCodePreference.key}" +
                    ", intervalListPreference.key: ${intervalListPreference.key}" +
                    ", pushSwitchPreference.key: ${pushSwitchPreference.key}")
        }

        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            val id = item.itemId
            if (id == android.R.id.home) {
                startActivity(Intent(activity, MainActivity::class.java))
                return true
            }
            return super.onOptionsItemSelected(item)
        }
    }

    companion object {
        var machineID = 0
        private val sBindPreferenceSummaryToValueListener =
            Preference.OnPreferenceChangeListener { preference, value ->
                if (preference is SwitchPreference) {
                    PreferenceManager
                        .getDefaultSharedPreferences(preference.context).edit()
                        .putBoolean(preference.key, value as Boolean).commit()
                } else {
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

                    PreferenceManager
                        .getDefaultSharedPreferences(preference.context).edit()
                        .putString(preference.key, stringValue).commit()
                }
                true
            }

        private fun isXLargeTablet(context: Context): Boolean {
            return context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_XLARGE
        }

        private fun bindPreferenceSummaryToValue(preference: Preference) {
            preference.onPreferenceChangeListener = sBindPreferenceSummaryToValueListener

            if (preference is SwitchPreference) {
                sBindPreferenceSummaryToValueListener.onPreferenceChange(
                    preference,
                    PreferenceManager
                        .getDefaultSharedPreferences(preference.context)
                        .getBoolean(preference.key, true)
                )
            } else {
                sBindPreferenceSummaryToValueListener.onPreferenceChange(
                    preference,
                    PreferenceManager
                        .getDefaultSharedPreferences(preference.context)
                        .getString(preference.key, "")
                )
            }
        }
    }
}
