package android.wings.websarva.dokusyokannrijavatokotlin.settings.fragments

import android.os.Bundle
import android.wings.websarva.dokusyokannrijavatokotlin.R
import androidx.preference.PreferenceFragmentCompat

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference, rootKey)
    }


}