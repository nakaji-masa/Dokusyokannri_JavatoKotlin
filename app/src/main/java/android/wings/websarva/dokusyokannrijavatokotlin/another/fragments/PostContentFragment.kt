package android.wings.websarva.dokusyokannrijavatokotlin.another.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.wings.websarva.dokusyokannrijavatokotlin.R
import android.wings.websarva.dokusyokannrijavatokotlin.another.AnotherUserContentPostAdapter
import android.wings.websarva.dokusyokannrijavatokotlin.another.activities.AnotherUserActivity
import android.wings.websarva.dokusyokannrijavatokotlin.another.fragments.base.BaseAnotherUserFragment
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.FireStoreHelper
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.model.UserInfoHelper
import android.wings.websarva.dokusyokannrijavatokotlin.interfaces.OnCommentClickListener
import android.wings.websarva.dokusyokannrijavatokotlin.post.fragments.CommentFragment
import android.wings.websarva.dokusyokannrijavatokotlin.utils.DividerHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_another_user_post_content.*


class PostContentFragment :BaseAnotherUserFragment() {

    private lateinit var adapterAnotherUser: AnotherUserContentPostAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_another_user_post_content, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val userJson = activity?.intent?.extras?.getString(AnotherUserActivity.ANOTHER_USER_KEY)
        val userInfo = Gson().fromJson<UserInfoHelper>(userJson, UserInfoHelper::class.java)
        val options = FireStoreHelper.getRecyclerOptionsFromUid(userInfo.uid)
        adapterAnotherUser = AnotherUserContentPostAdapter(userInfo, options)
        adapterAnotherUser.setCommentClickListener(object: OnCommentClickListener {
            override fun onCommentClickListener(userJson: String, bookJson: String) {
                val transaction = activity?.supportFragmentManager?.beginTransaction()
                transaction?.replace(R.id.anotherUserContainer, CommentFragment.newInstance(userJson, bookJson))
                transaction?.addToBackStack(null)
                transaction?.commit()
            }
        })
        contentPostRecyclerView.adapter = adapterAnotherUser
        contentPostRecyclerView.setHasFixedSize(true)
        contentPostRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        contentPostRecyclerView.addItemDecoration(DividerHelper.createDivider(requireContext()))
    }

    override fun onStart() {
        super.onStart()
        adapterAnotherUser.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapterAnotherUser.stopListening()
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            PostContentFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}