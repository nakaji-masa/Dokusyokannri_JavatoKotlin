package android.wings.websarva.dokusyokannrijavatokotlin.user.content.another.fragments.base

import android.os.Bundle
import android.wings.websarva.dokusyokannrijavatokotlin.user.content.another.activities.AnotherUserActivity
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.model.UserInfoHelper
import androidx.fragment.app.Fragment
import com.google.gson.Gson

open class BaseAnotherUserFragment: Fragment() {
    lateinit var user: UserInfoHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        user = requireActivity().intent.getParcelableExtra(AnotherUserActivity.ANOTHER_USER_KEY)
    }
}