package android.wings.websarva.dokusyokannrijavatokotlin.user.content.myshelf.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.wings.websarva.dokusyokannrijavatokotlin.R
import android.wings.websarva.dokusyokannrijavatokotlin.book.activities.PostForBookActivity
import android.wings.websarva.dokusyokannrijavatokotlin.databinding.FragmentMyPostBinding
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.AuthHelper
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.FireStoreHelper
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.model.BookHelper
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.model.UserInfoHelper
import android.wings.websarva.dokusyokannrijavatokotlin.interfaces.OnBookClickListener
import android.wings.websarva.dokusyokannrijavatokotlin.interfaces.OnCommentClickListener
import android.wings.websarva.dokusyokannrijavatokotlin.post.fragments.CommentFragment
import android.wings.websarva.dokusyokannrijavatokotlin.user.content.UsersPostAdapter
import android.wings.websarva.dokusyokannrijavatokotlin.user.content.myshelf.fragments.base.BaseMyFragment
import android.wings.websarva.dokusyokannrijavatokotlin.utils.DividerHelper
import androidx.recyclerview.widget.LinearLayoutManager


class MyPostFragment : BaseMyFragment() {

    private lateinit var adapter: UsersPostAdapter
    private var _binding: FragmentMyPostBinding? = null
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
        _binding = FragmentMyPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val options = FireStoreHelper.getRecyclerOptionsFromUid(AuthHelper.getUid())
        adapter = UsersPostAdapter(user, options)
        adapter.setCommentClickListener(object: OnCommentClickListener{
            override fun onCommentClickListener(user: UserInfoHelper, book: BookHelper) {
                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                transaction.replace(R.id.mainContainer, CommentFragment.newInstance(user, book))
                transaction.addToBackStack(null)
                transaction.commit()
            }
        })
        adapter.setBookClickListener(object: OnBookClickListener {
            override fun onBookClickListener(book: BookHelper) {
                PostForBookActivity.moveToPostForBookActivity(requireActivity(), book)
            }
        })
        binding.myPostRecyclerView.adapter = adapter
        binding.myPostRecyclerView.setHasFixedSize(true)
        binding.myPostRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.myPostRecyclerView.addItemDecoration(DividerHelper.createDivider(requireContext()))
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
            MyPostFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}