package android.wings.websarva.dokusyokannrijavatokotlin.another.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.wings.websarva.dokusyokannrijavatokotlin.R
import android.wings.websarva.dokusyokannrijavatokotlin.another.ContentPostAdapter
import android.wings.websarva.dokusyokannrijavatokotlin.another.fragments.base.BaseAnotherUserFragment
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.FireStoreHelper
import android.wings.websarva.dokusyokannrijavatokotlin.interfaces.OnCommentClickListener
import android.wings.websarva.dokusyokannrijavatokotlin.library.fragments.CommentFragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_post_content.*


class PostContentFragment :BaseAnotherUserFragment() {

    private lateinit var adapter: ContentPostAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_post_content, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val options = FireStoreHelper.getRecyclerOptionsFromUid()
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
        fun newInstance() =
            PostContentFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}