package android.wings.websarva.dokusyokannrijavatokotlin.detail.fragments

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.wings.websarva.dokusyokannrijavatokotlin.MyApplication
import android.wings.websarva.dokusyokannrijavatokotlin.R
import android.wings.websarva.dokusyokannrijavatokotlin.detail.activities.DetailActivity
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.AuthHelper
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.FireStoreHelper
import android.wings.websarva.dokusyokannrijavatokotlin.library.CommentAdapter
import android.wings.websarva.dokusyokannrijavatokotlin.realm.`object`.BookObject
import android.wings.websarva.dokusyokannrijavatokotlin.realm.`object`.UserInfoObject
import android.wings.websarva.dokusyokannrijavatokotlin.realm.config.RealmConfigObject
import android.wings.websarva.dokusyokannrijavatokotlin.utils.GlideHelper
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
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
            userRealm.where(UserInfoObject::class.java).equalTo("uid", AuthHelper.getUid()).findFirst()
        val bookObj = bookRealm.where(BookObject::class.java).equalTo("id", bookId).findFirst()

        // UI実装
        GlideHelper.viewUserImage(userObj?.imageUrl, userImage)
        userName.text = userObj?.userName
        userReadDate.text = bookObj?.date
        userActionPlan.text = bookObj?.actionPlan
        GlideHelper.viewBookImage(bookObj?.imageUrl, userBookImage)
        userBookTitle.text = bookObj?.title
        userAuthorName.text = bookObj?.author

        // handler
        val handler = Handler()

        GlobalScope.launch {
            // firestoreから取得する
            val bookData = FireStoreHelper.getBookData(bookId)

            val runnable = Runnable {
                // いいねとコメントの取得
                favoriteImage.setImageDrawable(ContextCompat.getDrawable(MyApplication.getAppContext(), R.drawable.ic_like))
                favoriteCount.text = bookData?.likedUserList?.size.toString()
                commentImage.setImageDrawable(
                    ContextCompat.getDrawable(
                        MyApplication.getAppContext(),
                        R.drawable.ic_comment
                    )
                )
                commentCount.text = bookData?.commentList?.size.toString()

                // リサイクラービューの実装
                detailCommentRecyclerView.setHasFixedSize(true)
                detailCommentRecyclerView.adapter = CommentAdapter(bookData?.commentList!!)
                detailCommentRecyclerView.layoutManager = LinearLayoutManager(activity)
            }
            // UI実装
            handler.post(runnable)
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