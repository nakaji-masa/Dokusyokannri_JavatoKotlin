package android.wings.websarva.dokusyokannrijavatokotlin.post


import android.view.LayoutInflater
import android.view.ViewGroup
import android.wings.websarva.dokusyokannrijavatokotlin.interfaces.OnCommentClickListener
import android.wings.websarva.dokusyokannrijavatokotlin.databinding.PostCellBinding
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.AuthHelper
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.FireStoreHelper
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.model.BookHelper
import android.wings.websarva.dokusyokannrijavatokotlin.interfaces.OnBookClickListener
import android.wings.websarva.dokusyokannrijavatokotlin.interfaces.OnUserImageClickListener
import android.wings.websarva.dokusyokannrijavatokotlin.utils.*
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class PostAdapter(options: FirestoreRecyclerOptions<BookHelper>) :
    FirestoreRecyclerAdapter<BookHelper, PostAdapter.ViewHolder>(options) {

    private lateinit var bookClickListener: OnBookClickListener
    private lateinit var commentListener: OnCommentClickListener
    private lateinit var userImageListener: OnUserImageClickListener
    private val scope = CoroutineScope(Dispatchers.Main)

    inner class ViewHolder(val itemBinding: PostCellBinding) : RecyclerView.ViewHolder(itemBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding = PostCellBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: BookHelper) {
        scope.launch {
            FireStoreHelper.getUserInfoFromUid(model.uid)?.let { user ->

                // ユーザーの画像と名前を表示
                GlideHelper.viewUserImage(user.userImageUrl, holder.itemBinding.userImage)
                holder.itemBinding.userName.text = user.userName

                // 本の情報を表示
                holder.itemBinding.userReadDate.text = DateHelper.formatDate(model.createdAt)
                holder.itemBinding.userActionPlan.text = model.action
                GlideHelper.viewBookImage(model.imageUrl, holder.itemBinding.userBookImage)
                holder.itemBinding.userBookTitle.text = model.title
                holder.itemBinding.userAuthorName.text = model.author
                holder.itemBinding.favoriteImage.setImageDrawable(PostHelper.getLikedDrawable(model.likedList))
                holder.itemBinding.favoriteCount.text = model.likedList.size.toString()
                holder.itemBinding.favoriteImage.setOnClickListener {
                    PostHelper.pushFavorite(model)
                }
                holder.itemBinding.commentImage.setImageDrawable(PostHelper.getCommentDrwable(model.commentedList))
                holder.itemBinding.commentCount.text = model.commentedList.size.toString()

                // コメント・本・ユーザー画像のリスナーを実装
                holder.itemBinding.bookLayout.setOnClickListener {
                    bookClickListener.onBookClickListener(model)
                }
                holder.itemBinding.commentImage.setOnClickListener {
                    commentListener.onCommentClickListener(user, model)
                }
                holder.itemBinding.userImage.setOnClickListener {
                    userImageListener.onUserImageClickListener(user)
                }

                // 自ユーザーか他ユーザーで判定
                if (model.uid == AuthHelper.getUid()) {
                    holder.itemBinding.userImage.isEnabled = false
                    holder.itemBinding.commentImage.isEnabled = false
                } else {
                    holder.itemBinding.userImage.isEnabled = true
                    holder.itemBinding.commentImage.isEnabled = true
                }
            }
        }

    }

    /**
     * フィールドのbookListenerをセットする
     * @param listener OnBookClickListener型のインターフェース
     */
    fun setBookClickListener(listener: OnBookClickListener) {
        this.bookClickListener = listener
    }

    /**
     * フィールドのcommentListenerをセットする
     * @param listener OnCommentClickListener型のインタフェース
     */
    fun setCommentClickListener(listener: OnCommentClickListener) {
        this.commentListener = listener
    }

    /**
     * フィールドのuserImageListenerをセットする
     * @param listener OnUserImageClickListener型のインターフェース
     */
    fun setUserImageClickListener(listener: OnUserImageClickListener) {
        this.userImageListener = listener
    }
}

