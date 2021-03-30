package android.wings.websarva.dokusyokannrijavatokotlin.user.content.another.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.wings.websarva.dokusyokannrijavatokotlin.R
import android.wings.websarva.dokusyokannrijavatokotlin.book.activities.PostForBookActivity
import android.wings.websarva.dokusyokannrijavatokotlin.databinding.FragmentAnotherPostBinding
import android.wings.websarva.dokusyokannrijavatokotlin.user.content.UsersPostAdapter
import android.wings.websarva.dokusyokannrijavatokotlin.user.content.another.fragments.base.BaseAnotherUserFragment
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.FireStoreHelper
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.model.BookHelper
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.model.UserInfoHelper
import android.wings.websarva.dokusyokannrijavatokotlin.interfaces.OnBookClickListener
import android.wings.websarva.dokusyokannrijavatokotlin.interfaces.OnCommentClickListener
import android.wings.websarva.dokusyokannrijavatokotlin.post.fragments.CommentFragment
import android.wings.websarva.dokusyokannrijavatokotlin.utils.DividerHelper
import androidx.recyclerview.widget.LinearLayoutManager


class AnotherPostFragment :BaseAnotherUserFragment() {

    private lateinit var adapter: UsersPostAdapter
    private var _binding: FragmentAnotherPostBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnotherPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val options = FireStoreHelper.getRecyclerOptionsFromUid(user.uid)
        adapter = UsersPostAdapter(user, options)
        adapter.setCommentClickListener(object: OnCommentClickListener {
            override fun onCommentClickListener(user: UserInfoHelper, book: BookHelper) {
                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                transaction.replace(R.id.anotherUserContainer, CommentFragment.newInstance(user, book))
                transaction.addToBackStack(null)
                transaction.commit()
            }
        })
        adapter.setBookClickListener(object: OnBookClickListener {
            override fun onBookClickListener(book: BookHelper) {
                PostForBookActivity.moveToPostForBookActivity(requireActivity(), book)
            }
        })
        binding.anotherPostRecyclerView.adapter = adapter
        binding.anotherPostRecyclerView.setHasFixedSize(true)
        binding.anotherPostRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.anotherPostRecyclerView.addItemDecoration(DividerHelper.createDivider(requireContext()))
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            AnotherPostFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}