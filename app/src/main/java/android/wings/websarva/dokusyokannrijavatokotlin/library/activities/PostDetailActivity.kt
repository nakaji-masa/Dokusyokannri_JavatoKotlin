package android.wings.websarva.dokusyokannrijavatokotlin.library.activities


import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.wings.websarva.dokusyokannrijavatokotlin.R
import android.wings.websarva.dokusyokannrijavatokotlin.library.CommentAdapter
import android.wings.websarva.dokusyokannrijavatokotlin.register.BookCommentHelper
import android.wings.websarva.dokusyokannrijavatokotlin.register.BookHelper
import android.wings.websarva.dokusyokannrijavatokotlin.register.UserInfo
import android.wings.websarva.dokusyokannrijavatokotlin.utils.AuthHelper
import android.wings.websarva.dokusyokannrijavatokotlin.utils.FireStoreHelper
import android.wings.websarva.dokusyokannrijavatokotlin.utils.GlideHelper
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_post_detail.*
import kotlinx.android.synthetic.main.library_cell.*


class PostDetailActivity : AppCompatActivity(), TextWatcher {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_detail)

        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        commentEdit.addTextChangedListener(this)

        // 選択した投稿・ユーザーのデータを取得
        val bookJson = intent.extras?.getString(BOOK_DATA_KEY)
        val userJson = intent.extras?.getString(USER_DATA_KEY)
        val bookData = Gson().fromJson<BookHelper>(bookJson, BookHelper::class.java)
        val userData = Gson().fromJson<UserInfo>(userJson, UserInfo::class.java)

        // 選択した投稿のデータを表示
        userReadDate.text = bookData.date
        userActionPlan.text = bookData.action
        Glide.with(this).load(bookData.imageUrl).into(userBookImage)
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
            val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(it.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            showRecyclerView(bookData.commentList)
            FireStoreHelper.savePostData(bookData)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onBackPressed() {
        finish()
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        postButton.isEnabled = commentEdit.text?.isNotEmpty()!!
    }

    override fun afterTextChanged(s: Editable?) {
    }

    /**
     * いいねのアイコンの色をセットし、いいねの数をセットするメソッド
     * @param likedUserList いいねの数が格納されているリスト
     */
    private fun showPostFavorite(likedUserList: List<String>) {
        favoriteImage.setImageDrawable(
            if (likedUserList.contains(AuthHelper.getUid())) {
                ContextCompat.getDrawable(this, R.drawable.ic_like)
            } else {
                ContextCompat.getDrawable(this, R.drawable.ic_no_like)
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
                ContextCompat.getDrawable(this, R.drawable.ic_comment)
            } else {
                ContextCompat.getDrawable(this, R.drawable.ic_no_comment)
            }
        )
        commentCount.text = commentList.size.toString()
    }

    /**
     * リサイクラービューの設定をするメソッド
     * @param commentList コメントのリスト
     */
    private fun showRecyclerView(commentList: List<BookCommentHelper>) {
        commentRecyclerView.layoutManager = LinearLayoutManager(this)
        commentRecyclerView.setHasFixedSize(true)
        commentRecyclerView.adapter = CommentAdapter(commentList)
    }

    companion object {
        const val BOOK_DATA_KEY = "BookHelper"
        const val USER_DATA_KEY = "userInfo"
    }

}