package android.wings.websarva.dokusyokannrijavatokotlin.book.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.wings.websarva.dokusyokannrijavatokotlin.R
import android.wings.websarva.dokusyokannrijavatokotlin.another.activities.AnotherUserActivity
import android.wings.websarva.dokusyokannrijavatokotlin.book.activities.PostForBookActivity
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.FireStoreHelper
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.model.BookHelper
import android.wings.websarva.dokusyokannrijavatokotlin.interfaces.OnBookClickListener
import android.wings.websarva.dokusyokannrijavatokotlin.interfaces.OnCommentClickListener
import android.wings.websarva.dokusyokannrijavatokotlin.interfaces.OnUserImageClickListener
import android.wings.websarva.dokusyokannrijavatokotlin.library.PostAdapter
import android.wings.websarva.dokusyokannrijavatokotlin.library.fragments.CommentFragment
import android.wings.websarva.dokusyokannrijavatokotlin.utils.GlideHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_post_for_book.*


class PostForBookFragment : Fragment() {
    private lateinit var adapter: PostAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_post_for_book, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val bookJson = activity?.intent?.getStringExtra(PostForBookActivity.BOOK_DATA_KEY)
        val book = Gson().fromJson<BookHelper>(bookJson, BookHelper::class.java)
        adapter = PostAdapter(FireStoreHelper.getRecyclerOptionsFromTitle(book.title))
        adapter.setUserImageClickListener(object : OnUserImageClickListener {
            override fun onUserImageClickListener(userJson: String) {
                AnotherUserActivity.moveToAnotherUserActivity(activity, userJson)
            }
        })
        adapter.setCommentClickListener(object : OnCommentClickListener {
            override fun onCommentClickListener(userJson: String, bookJson: String) {
                val transaction = activity?.supportFragmentManager?.beginTransaction()
                transaction?.replace(
                    R.id.postForBookContainer,
                    CommentFragment.newInstance(userJson, bookJson)
                )
                transaction?.addToBackStack(null)
                transaction?.commit()
            }
        })
        adapter.setBookClickListener(object : OnBookClickListener {
            // 本の投稿画面に遷移しているため、処理は何もしない。
            override fun onBookClickListener(bookJson: String) {
            }
        })
        bookPostRecyclerView.adapter = adapter
        bookPostRecyclerView.setHasFixedSize(true)
        bookPostRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        GlideHelper.viewBookImage(book.imageUrl, postForBookImage)
        postForBookTitle.text = book.title
        postForBookAuthor.text = getString(R.string.author, book.author)
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
            PostForBookFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}