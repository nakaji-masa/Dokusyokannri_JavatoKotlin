package android.wings.websarva.dokusyokannrijavatokotlin.another.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.wings.websarva.dokusyokannrijavatokotlin.R
import android.wings.websarva.dokusyokannrijavatokotlin.another.AnotherTabAdapter
import android.wings.websarva.dokusyokannrijavatokotlin.another.activities.AnotherUserActivity
import kotlinx.android.synthetic.main.fragment_contents_select.*


class ContentsSelectFragment : Fragment() {

    private lateinit var uid: String
    private lateinit var userJson: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            uid = it.getString(AnotherUserActivity.ANOTHER_UID_KEY, "")
            userJson = it.getString(AnotherUserActivity.ANOTHER_USER_KEY, "")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_contents_select, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        userContentsViewPager.adapter = AnotherTabAdapter(childFragmentManager, uid, userJson)
        userContentsTabLayout.setupWithViewPager(userContentsViewPager)
    }

    companion object {
        @JvmStatic
        fun newInstance(uid: String?, userJson: String?) =
            ContentsSelectFragment().apply {
                arguments = Bundle().apply {
                    putString(AnotherUserActivity.ANOTHER_UID_KEY, uid)
                    putString(AnotherUserActivity.ANOTHER_USER_KEY, userJson)
                }
            }
    }
}