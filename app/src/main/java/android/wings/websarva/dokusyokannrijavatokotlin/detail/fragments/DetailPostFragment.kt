package android.wings.websarva.dokusyokannrijavatokotlin.detail.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.wings.websarva.dokusyokannrijavatokotlin.R
import android.wings.websarva.dokusyokannrijavatokotlin.user.content.another.activities.AnotherUserActivity
import android.wings.websarva.dokusyokannrijavatokotlin.book.activities.PostForBookActivity
import android.wings.websarva.dokusyokannrijavatokotlin.databinding.FragmentDetailPostBinding
import android.wings.websarva.dokusyokannrijavatokotlin.databinding.PostCellBinding
import android.wings.websarva.dokusyokannrijavatokotlin.detail.activities.DetailActivity
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.AuthHelper
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.FireStoreHelper
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.model.UserInfoHelper
import android.wings.websarva.dokusyokannrijavatokotlin.interfaces.OnUserImageClickListener
import android.wings.websarva.dokusyokannrijavatokotlin.post.CommentAdapter
import android.wings.websarva.dokusyokannrijavatokotlin.realm.`object`.BookObject
import android.wings.websarva.dokusyokannrijavatokotlin.realm.`object`.UserInfoObject
import android.wings.websarva.dokusyokannrijavatokotlin.realm.manager.RealmManager
import android.wings.websarva.dokusyokannrijavatokotlin.utils.DividerHelper
import android.wings.websarva.dokusyokannrijavatokotlin.utils.GlideHelper
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import io.realm.Realm
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class DetailPostFragment : Fragment() {

    private lateinit var bookRealm: Realm
    private lateinit var userRealm: Realm
    private var _binding: FragmentDetailPostBinding? = null
    private val binding get() = _binding!!
    private var _postBinding: PostCellBinding? = null
    private val postBinding get() = _postBinding!!
    private val scope = CoroutineScope(Dispatchers.Main)
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
        bookRealm = RealmManager.getBookRealmInstance()
        userRealm = RealmManager.getUserRealmInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailPostBinding.inflate(inflater, container, false)
        _postBinding = binding.detailPost
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // realmからユーザー情報と本の情報を取得
        val userObj =
            userRealm.where(UserInfoObject::class.java).equalTo("uid", AuthHelper.getUid())
                .findFirst()
        val bookId = requireActivity().intent.getStringExtra(DetailActivity.BOOK_ID)
        val bookObj = bookRealm.where(BookObject::class.java).equalTo("id", bookId).findFirst()

        // UI実装
        GlideHelper.viewUserImage(userObj?.imageUrl, postBinding.userImage)
        postBinding.userName.text = userObj?.userName
        postBinding.userReadDate.text = bookObj?.date
        postBinding.userActionPlan.text = bookObj?.actionPlan
        GlideHelper.viewBookImage(bookObj?.imageUrl, postBinding.userBookImage)
        postBinding.userBookTitle.text = bookObj?.title
        postBinding.userAuthorName.text = bookObj?.author
        postBinding.favoriteImage.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ic_like
            )
        )
        postBinding.commentImage.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ic_comment
            )
        )

        scope.launch(Dispatchers.Main) {
            // 本の情報
            val bookData = FireStoreHelper.getBookDataFromDocId(bookId)
            if (bookData != null) {
                    // コメント・いいねの数を表示
                    postBinding.favoriteCount.text = bookData.likedList.size.toString()
                    postBinding.commentCount.text = bookData.commentedList.size.toString()

                    // 本のリスナーをセット
                    postBinding.bookLayout.setOnClickListener {
                        PostForBookActivity.moveToPostForBookActivity(requireActivity(), bookData)
                    }

                    // リサイクラービューの実装
                    binding.detailCommentRecyclerView.setHasFixedSize(true)
                    binding.detailCommentRecyclerView.layoutManager = LinearLayoutManager(requireContext())
                    binding.detailCommentRecyclerView.addItemDecoration(DividerHelper.createDivider(requireContext()))
                    val adapter = CommentAdapter(bookData.commentedList)
                    adapter.setUserImageClickListener(object : OnUserImageClickListener {
                        override fun onUserImageClickListener(user: UserInfoHelper) {
                            AnotherUserActivity.moveToAnotherUserActivity(activity, user)
                        }
                    })
                    binding.detailCommentRecyclerView.adapter = adapter
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _postBinding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            DetailPostFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}