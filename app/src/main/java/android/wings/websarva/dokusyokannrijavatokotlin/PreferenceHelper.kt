package android.wings.websarva.dokusyokannrijavatokotlin

import androidx.datastore.preferences.core.stringPreferencesKey

class PreferenceHelper {
    companion object {
        val userNameKey = stringPreferencesKey("userName")
        val password = stringPreferencesKey("password")
        val loginMode = stringPreferencesKey("loginMode")
    }
}