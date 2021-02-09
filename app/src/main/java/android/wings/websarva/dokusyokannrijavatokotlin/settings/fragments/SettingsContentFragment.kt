package android.wings.websarva.dokusyokannrijavatokotlin.settings.fragments

import android.os.Bundle
import android.wings.websarva.dokusyokannrijavatokotlin.R
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.AuthHelper
import android.wings.websarva.dokusyokannrijavatokotlin.user.fragments.UserProfileFragment
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat

class SettingsContentFragment: PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference, rootKey)

        // プロフィール変更
        val userInfoPref = findPreference<Preference>(USER_INFO_KEY)
        userInfoPref?.setOnPreferenceClickListener {
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.addToBackStack(null)
            transaction?.replace(
                R.id.mainContainer,
                UserProfileFragment.newInstance(UserProfileFragment.EDIT_MODE)
            )
            transaction?.commit()
            true
        }

        // ログアウト
        val logoutPref = findPreference<Preference>(LOGOUT_KEY)
        logoutPref?.setOnPreferenceClickListener {
            AuthHelper.signOut()
            activity?.finish()
            true
        }
    }

    companion object {
        const val USER_INFO_KEY = "user_info_update"
        const val VERSION_KEY = "app_version"
        const val TERM_KEY = "app_term"
        const val PRIVACY_POLICY_KEY = "privacy_policy"
        const val LOGOUT_KEY = "logout"

        @JvmStatic
        fun newInstance() =
            SettingsContentFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

}