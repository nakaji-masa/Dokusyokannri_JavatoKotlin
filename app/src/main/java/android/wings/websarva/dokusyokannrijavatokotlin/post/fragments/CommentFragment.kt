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
import android.wings.websarva.dokusyokannrijavatokotlin.databinding.FragmentCommentBinding
import android.wings.websarva.dokusyokannrijavatokotlin.databinding.PostCellBinding
import android.wings.websarva.dokusyokannrijavatokotlin.user.content.another.activities.AnotherUserActivity
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
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide

class CommentFragment : Fragment() {

    private lateinit var user: UserInfoHelper
    private lateinit var book: BookHelper
    private var activityName: String? = null
    private var supportActionBar: ActionBar? = null
    private var _binding: FragmentCommentBinding? = null
    private val binding get() = _binding!!
    private var _postBinding: PostCellBinding? = null
    private val postBinding get() = _postBinding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityName = activity?.localClassName
        arguments?.let { it ->
            it.getParcelable<UserInfoHelper>(USER_DATA_KEY)?.let { data ->
                user = data
            }

            it.getParcelable<BookHelper>(BOOK_DATA_KEY)?.let { data ->
                book = data
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        _binding = FragmentCommentBinding.inflate(inflater, container, false)
        _postBinding = binding.post
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // アクションバーの設定
        if (activityName != getString(R.string.another_user_activity)) {
            supportActionBar = (activity as AppCompatActivity).supportActionBar
            supportActionBar?.setDisplayShowHomeEnabled(true)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.show()
        }

        // 選択した投稿のデータを表示
        postBinding.userReadDate.text = DateHelper.formatDate(book.createdAt)
        postBinding.userActionPlan.text = book.action
        Glide.with(requireContext()).load(book.imageUrl).into(postBinding.userBookImage)
        postBinding.userBookTitle.text = book.title
        postBinding.userAuthorName.text = book.author
        postBinding.userName.text = user.userName
        GlideHelper.viewUserImage(user.userImageUrl, postBinding.userImage)


        // いいねのアイコンを表示する
        showPostLike(book.likedList)

        // コメントのアイコンを表示する
        showComment(book.commentedList)

        // リサイクラービューを表示する
        showRecyclerView(book.commentedList)

        // テキストの監視
        binding.commentEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.postButton.isEnabled = binding.commentEdit.text?.isNotBlank()!!
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })

        // postButtonの設定
        binding.postButton.setOnClickListener {
            // コメントのデータをセット
            book.commentedList.add(CommentHelper(comment = binding.commentEdit.text.toString()))
            book.commentedCount = book.commentedList.size

            // キーボードを閉じる
            binding.commentEdit.text?.clear()
            binding.commentEdit.clearFocus()
            val inputManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(it.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)

            // リサイクラービューの更新
            showRecyclerView(book.commentedList)

            // コメントの数の更新
            showComment(book.commentedList)
            FireStoreHelper.saveBook(book)
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
        postBinding.favoriteImage.setImageDrawable(
            if (likedList.any {it.likedUserUid == AuthHelper.getUid()}) {
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_like)
            } else {
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_no_like)
            }
        )
        postBinding.favoriteCount.text = likedList.size.toString()
    }

    /**
     * コメントのアイコンの色をセットし、コメントの数をセットするメソッド
     * @param commentList コメントの数を格納されているメソッド
     */
    private fun showComment(commentList: List<CommentHelper>) {
        postBinding.commentImage.setImageDrawable(
            if (commentList.any { it.commentedUserUid == AuthHelper.getUid() }) {
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_comment)
            } else {
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_no_comment)
            }
        )
        postBinding.commentCount.text = commentList.size.toString()
    }

    /**
     * リサイクラービューの設定をするメソッド
     * @param commentList コメントのリスト
     */
    private fun showRecyclerView(commentList: List<CommentHelper>) {
        binding.commentRecyclerView.addItemDecoration(DividerHelper.createDivider(requireContext()))
        binding.commentRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.commentRecyclerView.setHasFixedSize(true)
        val adapter = CommentAdapter(commentList)
        binding.commentRecyclerView.adapter = adapter
        adapter.setUserImageClickListener(object : OnUserImageClickListener {
            override fun onUserImageClickListener(user: UserInfoHelper) {
                AnotherUserActivity.moveToAnotherUserActivity(requireActivity(), user)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _postBinding = null
        _binding = null
    }

    companion object {
        private const val USER_DATA_KEY = "user_data_key"
        private const val BOOK_DATA_KEY = "book_data_key"

        @JvmStatic
        fun newInstance(user: UserInfoHelper, book: BookHelper) =
            CommentFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(USER_DATA_KEY, user)
                    putParcelable(BOOK_DATA_KEY, book)
                }
            }
    }
}