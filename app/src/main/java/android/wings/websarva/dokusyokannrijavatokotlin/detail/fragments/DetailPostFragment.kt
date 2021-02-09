package android.wings.websarva.dokusyokannrijavatokotlin.detail.fragments


import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.wings.websarva.dokusyokannrijavatokotlin.MyApplication
import android.wings.websarva.dokusyokannrijavatokotlin.R
import android.wings.websarva.dokusyokannrijavatokotlin.another.activities.AnotherUserActivity
import android.wings.websarva.dokusyokannrijavatokotlin.book.activities.PostForBookActivity
import android.wings.websarva.dokusyokannrijavatokotlin.detail.activities.DetailActivity
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.AuthHelper
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.FireStoreHelper
import android.wings.websarva.dokusyokannrijavatokotlin.interfaces.OnUserImageClickListener
import android.wings.websarva.dokusyokannrijavatokotlin.library.CommentAdapter
import android.wings.websarva.dokusyokannrijavatokotlin.realm.`object`.BookObject
import android.wings.websarva.dokusyokannrijavatokotlin.realm.`object`.UserInfoObject
import android.wings.websarva.dokusyokannrijavatokotlin.realm.config.RealmConfigObject
import android.wings.websarva.dokusyokannrijavatokotlin.utils.GlideHelper
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_detail_post.*
import kotlinx.android.synthetic.main.library_cell.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class DetailPostFragment : Fragment() {

    private lateinit var bookId: String
    private lateinit var bookRealm: Realm
    private lateinit var userRealm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            bookId = it.getString(DetailActivity.BOOK_ID, "")
        }
        bookRealm = Realm.getInstance(RealmConfigObject.bookConfig)
        userRealm = Realm.getInstance(RealmConfigObject.userConfig)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_detail_post, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // realmからユーザー情報と本の情報を取得
        val userObj =
            userRealm.where(UserInfoObject::class.java).equalTo("uid", AuthHelper.getUid())
                .findFirst()
        val bookObj = bookRealm.where(BookObject::class.java).equalTo("id", bookId).findFirst()

        // UI実装
        GlideHelper.viewUserImage(userObj?.imageUrl, userImage)
        userName.text = userObj?.userName
        userReadDate.text = bookObj?.date
        userActionPlan.text = bookObj?.actionPlan
        GlideHelper.viewBookImage(bookObj?.imageUrl, userBookImage)
        userBookTitle.text = bookObj?.title
        userAuthorName.text = bookObj?.author
        favoriteImage.setImageDrawable(
            ContextCompat.getDrawable(
                MyApplication.getAppContext(),
                R.drawable.ic_like
            )
        )
        commentImage.setImageDrawable(
            ContextCompat.getDrawable(
                MyApplication.getAppContext(),
                R.drawable.ic_comment
            )
        )

        // handler
        val handler = Handler()

        GlobalScope.launch {

            // 本の情報
            val bookData = FireStoreHelper.getBookData(bookId)
            if (bookData != null) {
                handler.post {
                    // コメント・いいねの数を表示
                    favoriteCount.text = bookData.likedUserList.size.toString()
                    commentCount.text = bookData.commentList.size.toString()

                    // 本のリスナーをセット
                    bookLayout.setOnClickListener {
                        val bookJson = Gson().toJson(bookData)
                        PostForBookActivity.moveToPostForBookActivity(activity, bookJson)
                    }

                    // リサイクラービューの実装
                    detailCommentRecyclerView.setHasFixedSize(true)
                    val adapter = CommentAdapter(bookData.commentList)
                    adapter.setUserImageClickListener(object : OnUserImageClickListener {
                        override fun onUserImageClickListener(userJson: String) {
                            AnotherUserActivity.moveToAnotherUserActivity(activity, userJson)
                        }
                    })
                    detailCommentRecyclerView.adapter = adapter
                    detailCommentRecyclerView.layoutManager = LinearLayoutManager(requireContext())
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        bookRealm.close()
        userRealm.close()
    }

    companion object {
        @JvmStatic
        fun newInstance(id: String?) =
            DetailPostFragment().apply {
                arguments = Bundle().apply {
                    putString(DetailActivity.BOOK_ID, id)
                }
            }
    }
}