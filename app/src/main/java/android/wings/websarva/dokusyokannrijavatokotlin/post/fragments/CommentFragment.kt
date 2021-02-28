package android.wings.websarva.dokusyokannrijavatokotlin.post.fragments

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
import android.wings.websarva.dokusyokannrijavatokotlin.post.CommentAdapter
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.model.CommentHelper
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.model.BookHelper
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.model.LikeHelper
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.model.UserInfoHelper
import android.wings.websarva.dokusyokannrijavatokotlin.interfaces.OnUserImageClickListener
import android.wings.websarva.dokusyokannrijavatokotlin.utils.DateHelper
import android.wings.websarva.dokusyokannrijavatokotlin.utils.DividerHelper
import android.wings.websarva.dokusyokannrijavatokotlin.utils.GlideHelper
import android.wings.websarva.dokusyokannrijavatokotlin.utils.PostHelper
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_comment.*
import kotlinx.android.synthetic.main.post_cell.*

class CommentFragment : Fragment() {

    private lateinit var userJson: String
    private lateinit var bookJson: String
    private var activityName: String? = null
    private var supportActionBar: ActionBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityName = activity?.localClassName
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
        if (activityName != getString(R.string.another_user_activity)) {
            supportActionBar = (activity as AppCompatActivity).supportActionBar
            supportActionBar?.setDisplayShowHomeEnabled(true)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.show()
        }

        val userData = Gson().fromJson<UserInfoHelper>(userJson, UserInfoHelper::class.java)
        val bookData = Gson().fromJson<BookHelper>(bookJson, BookHelper::class.java)

        // 選択した投稿のデータを表示
        userReadDate.text = DateHelper.formatDate(bookData.createdAt)
        userActionPlan.text = bookData.action
        Glide.with(requireContext()).load(bookData.imageUrl).into(userBookImage)
        userBookTitle.text = bookData.title
        userAuthorName.text = bookData.author
        userName.text = userData.userName
        GlideHelper.viewUserImage(userData.userImageUrl, userImage)

        // いいねのアイコンを表示する
        showPostLike(bookData.likedList)

        // コメントのアイコンを表示する
        showComment(bookData.commentedList)

        // リサイクラービューを表示する
        showRecyclerView(bookData.commentedList)

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
            // コメントのデータをセット
            bookData.commentedList.add(CommentHelper(comment = commentEdit.text.toString()))
            bookData.commentedCount = bookData.commentedList.size

            // キーボードを閉じる
            commentEdit.text?.clear()
            commentEdit.clearFocus()
            val inputManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(it.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)

            // リサイクラービューの更新
            showRecyclerView(bookData.commentedList)

            // コメントの数の更新
            showComment(bookData.commentedList)
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
        if (activityName == getString(R.string.main_activity)) {
            supportActionBar?.hide()
        }
    }

    /**
     * いいねのアイコンの色をセットし、いいねの数をセットするメソッド
     * @param likedList いいねの数が格納されているリスト
     */
    private fun showPostLike(likedList: List<LikeHelper>) {
        favoriteImage.setImageDrawable(
            if (PostHelper.hasLiked(likedList)) {
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_like)
            } else {
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_no_like)
            }
        )
        favoriteCount.text = likedList.size.toString()
    }

    /**
     * コメントのアイコンの色をセットし、コメントの数をセットするメソッド
     * @param commentList コメントの数を格納されているメソッド
     */
    private fun showComment(commentList: List<CommentHelper>) {
        commentImage.setImageDrawable(
            if (commentList.any { it.commentedUserUid == AuthHelper.getUid() }) {
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
    private fun showRecyclerView(commentList: List<CommentHelper>) {
        commentRecyclerView.addItemDecoration(DividerHelper.createDivider(requireContext()))
        commentRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        commentRecyclerView.setHasFixedSize(true)
        val adapter = CommentAdapter(commentList)
        commentRecyclerView.adapter = adapter
        adapter.setUserImageClickListener(object : OnUserImageClickListener {
            override fun onUserImageClickListener(userJson: String) {
                AnotherUserActivity.moveToAnotherUserActivity(activity, userJson)
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