package com.dhbw.cas.integra.ui.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.dhbw.cas.integra.R

class SettingsFragment: PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }
}