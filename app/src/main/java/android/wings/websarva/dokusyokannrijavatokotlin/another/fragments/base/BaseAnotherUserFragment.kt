package android.wings.websarva.dokusyokannrijavatokotlin.another.fragments.base

import android.os.Bundle
import android.wings.websarva.dokusyokannrijavatokotlin.another.activities.AnotherUserActivity
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.model.UserInfoHelper
import androidx.fragment.app.Fragment
import com.google.gson.Gson

open class BaseAnotherUserFragment: Fragment() {
    private var userJson: String? = null
    lateinit var userInfo: UserInfoHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userJson = activity?.intent?.getStringExtra(AnotherUserActivity.ANOTHER_USER_KEY)
        userInfo = Gson().fromJson<UserInfoHelper>(userJson, UserInfoHelper::class.java)
    }
}