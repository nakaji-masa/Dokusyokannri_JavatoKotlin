package android.wings.websarva.dokusyokannrijavatokotlin.library.fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.wings.websarva.dokusyokannrijavatokotlin.R
import android.wings.websarva.dokusyokannrijavatokotlin.another.activities.AnotherUserActivity
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.AuthHelper
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.FireStoreHelper
import android.wings.websarva.dokusyokannrijavatokotlin.library.CommentAdapter
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.model.BookCommentHelper
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.model.BookHelper
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.model.UserInfoHelper
import android.wings.websarva.dokusyokannrijavatokotlin.interfaces.OnUserImageClickListener
import android.wings.websarva.dokusyokannrijavatokotlin.utils.GlideHelper
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_comment.*
import kotlinx.android.synthetic.main.library_cell.*

class CommentFragment : Fragment() {

    private lateinit var userJson: String
    private lateinit var bookJson: String
    private var supportActionBar: ActionBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userJson = it.getString(USER_DATA_KEY, "")
            bookJson = it.getString(BOOK_DATA_KEY, "")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_comment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // アクションバーの設定
        supportActionBar = (activity as AppCompatActivity).supportActionBar
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val userData = Gson().fromJson<UserInfoHelper>(userJson, UserInfoHelper::class.java)
        val bookData = Gson().fromJson<BookHelper>(bookJson, BookHelper::class.java)

        // 選択した投稿のデータを表示
        userReadDate.text = bookData.date
        userActionPlan.text = bookData.action
        Glide.with(requireContext()).load(bookData.imageUrl).into(userBookImage)
        userBookTitle.text = bookData.title
        userAuthorName.text = bookData.author
        userName.text = userData.userName
        GlideHelper.viewUserImage(userData.userImageUrl, userImage)

        // いいねのアイコンを表示する
        showPostFavorite(bookData.likedUserList)

        // コメントのアイコンを表示する
        showPostComment(bookData.commentList)

        // リサイクラービューを表示する
        showRecyclerView(bookData.commentList)

        // テキストの監視
        commentEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                postButton.isEnabled = commentEdit.text?.isNotBlank()!!
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })

        // postButtonの設定
        postButton.setOnClickListener {
            bookData.commentList.add(
                BookCommentHelper(
                    AuthHelper.getUid(),
                    commentEdit.text.toString()
                )
            )
            commentEdit.text?.clear()
            commentEdit.clearFocus()
            val inputManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(it.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            showRecyclerView(bookData.commentList)
            FireStoreHelper.savePostData(bookData)
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                activity?.supportFragmentManager?.popBackStack()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setDisplayShowHomeEnabled(false)
    }

    /**
     * いいねのアイコンの色をセットし、いいねの数をセットするメソッド
     * @param likedUserList いいねの数が格納されているリスト
     */
    private fun showPostFavorite(likedUserList: List<String>) {
        favoriteImage.setImageDrawable(
            if (likedUserList.contains(AuthHelper.getUid())) {
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_like)
            } else {
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_no_like)
            }
        )
        favoriteCount.text = likedUserList.size.toString()
    }

    /**
     * コメントのアイコンの色をセットし、コメントの数をセットするメソッド
     * @param commentList コメントの数を格納されているメソッド
     */
    private fun showPostComment(commentList: List<BookCommentHelper>) {
        commentImage.setImageDrawable(
            if (commentList.any { it.userUid == AuthHelper.getUid() }) {
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_comment)
            } else {
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_no_comment)
            }
        )
        commentCount.text = commentList.size.toString()
    }

    /**
     * リサイクラービューの設定をするメソッド
     * @param commentList コメントのリスト
     */
    private fun showRecyclerView(commentList: List<BookCommentHelper>) {
        commentRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        commentRecyclerView.setHasFixedSize(true)
        val adapter = CommentAdapter(commentList)
        commentRecyclerView.adapter = adapter
        adapter.setUserImageClickListener(object : OnUserImageClickListener {
            override fun onUserImageClickListener(uid: String, userJson: String) {
                AnotherUserActivity.moveToAnotherUserActivity(activity, uid, userJson)
            }
        })
    }

    companion object {
        private const val USER_DATA_KEY = "user_data_key"
        private const val BOOK_DATA_KEY = "book_data_key"

        @JvmStatic
        fun newInstance(userJson: String, bookJson: String) =
            CommentFragment().apply {
                arguments = Bundle().apply {
                    putString(USER_DATA_KEY, userJson)
                    putString(BOOK_DATA_KEY, bookJson)
                }
            }
    }
}