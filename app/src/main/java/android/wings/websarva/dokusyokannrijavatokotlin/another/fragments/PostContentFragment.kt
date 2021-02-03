package android.wings.websarva.dokusyokannrijavatokotlin.another.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.wings.websarva.dokusyokannrijavatokotlin.R
import android.wings.websarva.dokusyokannrijavatokotlin.another.ContentPostAdapter
import android.wings.websarva.dokusyokannrijavatokotlin.another.activities.AnotherUserActivity
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.FireStoreHelper
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.model.UserInfoHelper
import android.wings.websarva.dokusyokannrijavatokotlin.interfaces.OnCommentClickListener
import android.wings.websarva.dokusyokannrijavatokotlin.library.fragments.CommentFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_post_content.*


class PostContentFragment : Fragment() {

    private lateinit var uid: String
    private lateinit var userJson: String
    private lateinit var adapter: ContentPostAdapter

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
        return inflater.inflate(R.layout.fragment_post_content, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val userInfo = Gson().fromJson<UserInfoHelper>(userJson, UserInfoHelper::class.java)
        val options = FireStoreHelper.getUserOfRecyclerOptions(uid)
        adapter = ContentPostAdapter(userInfo, options)
        adapter.setCommentClickListener(object: OnCommentClickListener {
            override fun onCommentClickListener(userJson: String, bookJson: String) {
                val transaction = activity?.supportFragmentManager?.beginTransaction()
                transaction?.replace(R.id.anotherUserContainer, CommentFragment.newInstance(userJson, bookJson))
                transaction?.addToBackStack(null)
                transaction?.commit()
            }
        })
        contentPostRecyclerView.adapter = adapter
        contentPostRecyclerView.setHasFixedSize(true)
        contentPostRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    companion object {
        @JvmStatic
        fun newInstance(uid: String?, userJson: String) =
            PostContentFragment().apply {
                arguments = Bundle().apply {
                    putString(AnotherUserActivity.ANOTHER_UID_KEY, uid)
                    putString(AnotherUserActivity.ANOTHER_USER_KEY, userJson)
                }
            }
    }
}