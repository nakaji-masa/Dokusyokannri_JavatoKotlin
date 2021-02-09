package android.wings.websarva.dokusyokannrijavatokotlin.another.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.wings.websarva.dokusyokannrijavatokotlin.R
import android.wings.websarva.dokusyokannrijavatokotlin.another.activities.AnotherUserActivity
import android.wings.websarva.dokusyokannrijavatokotlin.another.fragments.base.BaseAnotherUserFragment
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.model.UserInfoHelper
import android.wings.websarva.dokusyokannrijavatokotlin.utils.GlideHelper
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_another_user.*


class AnotherUserFragment : BaseAnotherUserFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_another_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        GlideHelper.viewUserImage(userInfo.userImageUrl, anotherUserImage)
        anotherUserName.text = userInfo.userName
        anotherUserIntroduction.text = userInfo.introduction

        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.add(
            R.id.userContentContainer,
            ContentsSelectFragment.newInstance()
        )
        transaction?.commit()
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            AnotherUserFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}