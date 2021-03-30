package android.wings.websarva.dokusyokannrijavatokotlin.post.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.wings.websarva.dokusyokannrijavatokotlin.R
import android.wings.websarva.dokusyokannrijavatokotlin.user.content.another.activities.AnotherUserActivity
import android.wings.websarva.dokusyokannrijavatokotlin.book.activities.PostForBookActivity
import android.wings.websarva.dokusyokannrijavatokotlin.databinding.FragmentPostBinding
import android.wings.websarva.dokusyokannrijavatokotlin.databinding.SearchActionBarLayoutBinding
import android.wings.websarva.dokusyokannrijavatokotlin.post.PostAdapter
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.FireStoreHelper
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.model.BookHelper
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.model.UserInfoHelper
import android.wings.websarva.dokusyokannrijavatokotlin.interfaces.OnBookClickListener
import android.wings.websarva.dokusyokannrijavatokotlin.interfaces.OnCommentClickListener
import android.wings.websarva.dokusyokannrijavatokotlin.interfaces.OnUserImageClickListener
import android.wings.websarva.dokusyokannrijavatokotlin.utils.DividerHelper
import androidx.recyclerview.widget.LinearLayoutManager


class PostFragment : Fragment() {

    lateinit var adapter: PostAdapter
    private var _binding: FragmentPostBinding? = null
    private val binding get() = _binding!!
    private var _searchBinding: SearchActionBarLayoutBinding? = null
    private val searchBinding get() = _searchBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostBinding.inflate(inflater, container, false)
        _searchBinding = binding.searchBarLayout
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        searchBinding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (searchBinding.searchEditText.text.isNullOrEmpty()) {
                    adapter.updateOptions(FireStoreHelper.getAllRecyclerOptions())
                } else {
                    adapter.updateOptions(
                        FireStoreHelper.getRecyclerOptionsFromSearchText(
                            searchBinding.searchEditText.text.toString()
                        )
                    )
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        // リサイクラービューの設定
        binding.postList.addItemDecoration(DividerHelper.createDivider(requireContext()))
        binding.postList.layoutManager = LinearLayoutManager(requireContext())
        binding.postList.setHasFixedSize(true)
        adapter = PostAdapter(FireStoreHelper.getAllRecyclerOptions())
        binding.postList.adapter = adapter

        adapter.setCommentClickListener(object : OnCommentClickListener {
            override fun onCommentClickListener(user: UserInfoHelper, book: BookHelper) {
                val transaction = activity?.supportFragmentManager?.beginTransaction()
                transaction?.addToBackStack(null)
                transaction?.replace(
                    R.id.mainContainer,
                    CommentFragment.newInstance(user, book)
                )
                transaction?.commit()
            }
        })

        adapter.setUserImageClickListener(object : OnUserImageClickListener {
            override fun onUserImageClickListener(user: UserInfoHelper) {
                AnotherUserActivity.moveToAnotherUserActivity(activity, user)
            }
        })

        adapter.setBookClickListener(object : OnBookClickListener {
            override fun onBookClickListener(book: BookHelper) {
                PostForBookActivity.moveToPostForBookActivity(requireActivity(), book)
            }
        })
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
        _searchBinding = null
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            PostFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

}