package android.wings.websarva.dokusyokannrijavatokotlin.library.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.wings.websarva.dokusyokannrijavatokotlin.R
import android.wings.websarva.dokusyokannrijavatokotlin.another.activities.AnotherUserActivity
import android.wings.websarva.dokusyokannrijavatokotlin.book.activities.PostForBookActivity
import android.wings.websarva.dokusyokannrijavatokotlin.library.PostAdapter
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.FireStoreHelper
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.model.BookHelper
import android.wings.websarva.dokusyokannrijavatokotlin.interfaces.OnBookClickListener
import android.wings.websarva.dokusyokannrijavatokotlin.interfaces.OnCommentClickListener
import android.wings.websarva.dokusyokannrijavatokotlin.interfaces.OnUserImageClickListener
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import kotlinx.android.synthetic.main.fragment_post.*
import kotlinx.android.synthetic.main.search_action_bar_layout.*


class LibraryFragment : Fragment() {

    lateinit var adapter: PostAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_post, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        searchEditText.addTextChangedListener(object :TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (searchEditText.text.isNullOrEmpty()) {
                    updateAdapter(FireStoreHelper.getAllRecyclerOptions())
                } else {
                    updateAdapter(FireStoreHelper.getRecyclerOptionsFromSearchText(searchEditText.text.toString()))
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        usersBookList.layoutManager = LinearLayoutManager(requireContext())
        usersBookList.setHasFixedSize(true)
        adapter = PostAdapter(FireStoreHelper.getAllRecyclerOptions())
        usersBookList.adapter = adapter
        adapter.setCommentClickListener(object : OnCommentClickListener {
            override fun onCommentClickListener(userJson: String, bookJson: String) {
                val transaction = activity?.supportFragmentManager?.beginTransaction()
                transaction?.addToBackStack(null)
                transaction?.replace(
                    R.id.mainContainer,
                    CommentFragment.newInstance(userJson, bookJson)
                )
                transaction?.commit()
            }
        })

        adapter.setUserImageClickListener(object : OnUserImageClickListener {
            override fun onUserImageClickListener(userJson: String) {
                AnotherUserActivity.moveToAnotherUserActivity(activity, userJson)
            }
        })

        adapter.setBookClickListener(object : OnBookClickListener {
            override fun onBookClickListener(bookJson: String) {
                PostForBookActivity.moveToPostForBookActivity(activity, bookJson)
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

    /**
     * adapterのoptionsを更新するメソッドです
     * @param options
     */
    private fun updateAdapter(options: FirestoreRecyclerOptions<BookHelper>) {
        adapter.updateOptions(options)
    }


    companion object {
        @JvmStatic
        fun newInstance() =
            LibraryFragment().apply {
                arguments = Bundle().apply {
                }
            }

        /**
         * @param activity
         * アクションバーを初期化するメソッドです。
         */
        fun initializeActionBar(activity: FragmentActivity?) {
            val supportActionBar = (activity as AppCompatActivity).supportActionBar
            supportActionBar?.setDisplayShowCustomEnabled(false)
            supportActionBar?.setDisplayShowTitleEnabled(true)
        }

    }

}