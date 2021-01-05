package android.wings.websarva.dokusyokannrijavatokotlin

import androidx.datastore.preferences.core.preferencesKey

class PreferenceHelper {
    companion object {
        val userNameKey = preferencesKey<String>("userName")
        val password = preferencesKey<String>("password")
        val loginMode = preferencesKey<Boolean>("loginMode")
    }
}