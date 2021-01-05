package android.wings.websarva.dokusyokannrijavatokotlin.settings.fragments

import android.os.Bundle
import android.wings.websarva.dokusyokannrijavatokotlin.R
import android.wings.websarva.dokusyokannrijavatokotlin.utils.AuthHelper
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.firebase.auth.FirebaseAuth

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference, rootKey)

        val logoutPref =findPreference<Preference>("logout")
        logoutPref?.setOnPreferenceClickListener {
            AuthHelper.signOut()
            activity?.finish()
            true
        }
    }


}