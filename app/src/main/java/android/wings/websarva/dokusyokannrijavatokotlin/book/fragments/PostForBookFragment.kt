package android.wings.websarva.dokusyokannrijavatokotlin.book.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.wings.websarva.dokusyokannrijavatokotlin.R
import android.wings.websarva.dokusyokannrijavatokotlin.user.content.another.activities.AnotherUserActivity
import android.wings.websarva.dokusyokannrijavatokotlin.book.activities.PostForBookActivity
import android.wings.websarva.dokusyokannrijavatokotlin.databinding.FragmentPostForBookBinding
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.FireStoreHelper
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.model.BookHelper
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.model.UserInfoHelper
import android.wings.websarva.dokusyokannrijavatokotlin.interfaces.OnBookClickListener
import android.wings.websarva.dokusyokannrijavatokotlin.interfaces.OnCommentClickListener
import android.wings.websarva.dokusyokannrijavatokotlin.interfaces.OnUserImageClickListener
import android.wings.websarva.dokusyokannrijavatokotlin.post.PostAdapter
import android.wings.websarva.dokusyokannrijavatokotlin.post.fragments.CommentFragment
import android.wings.websarva.dokusyokannrijavatokotlin.utils.DividerHelper
import android.wings.websarva.dokusyokannrijavatokotlin.utils.GlideHelper
import androidx.recyclerview.widget.LinearLayoutManager


class PostForBookFragment : Fragment() {
    private var _binding: FragmentPostForBookBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: PostAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostForBookBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val book = requireActivity().intent.getParcelableExtra<BookHelper>(PostForBookActivity.BOOK_DATA_KEY)
        adapter = PostAdapter(FireStoreHelper.getRecyclerOptionsFromTitle(book.title))
        adapter.setUserImageClickListener(object : OnUserImageClickListener {
            override fun onUserImageClickListener(user: UserInfoHelper) {
                AnotherUserActivity.moveToAnotherUserActivity(activity, user)
            }
        })
        adapter.setCommentClickListener(object : OnCommentClickListener {
            override fun onCommentClickListener(user: UserInfoHelper, book: BookHelper) {
                val transaction = activity?.supportFragmentManager?.beginTransaction()
                transaction?.replace(
                    R.id.postForBookContainer,
                    CommentFragment.newInstance(user, book)
                )
                transaction?.addToBackStack(null)
                transaction?.commit()
            }
        })
        adapter.setBookClickListener(object : OnBookClickListener {
            // 本の投稿画面に遷移しているため、処理は何もしない。
            override fun onBookClickListener(book: BookHelper) {
            }
        })

        // リサイクラービューの設定
        binding.bookPostRecyclerView.addItemDecoration(DividerHelper.createDivider(requireContext()))
        binding.bookPostRecyclerView.setHasFixedSize(true)
        binding.bookPostRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.bookPostRecyclerView.adapter = adapter

        GlideHelper.viewBookImage(book.imageUrl, binding.postForBookImage)
        binding.postForBookTitle.text = book.title
        binding.postForBookAuthor.text = getString(R.string.author, book.author)
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
            PostForBookFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}