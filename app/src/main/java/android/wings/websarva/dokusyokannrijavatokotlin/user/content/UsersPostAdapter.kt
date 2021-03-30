package android.wings.websarva.dokusyokannrijavatokotlin.user.content

import android.view.LayoutInflater
import android.view.ViewGroup
import android.wings.websarva.dokusyokannrijavatokotlin.databinding.PostCellBinding
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.model.BookHelper
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.model.UserInfoHelper
import android.wings.websarva.dokusyokannrijavatokotlin.interfaces.OnBookClickListener
import android.wings.websarva.dokusyokannrijavatokotlin.interfaces.OnCommentClickListener
import android.wings.websarva.dokusyokannrijavatokotlin.utils.DateHelper
import android.wings.websarva.dokusyokannrijavatokotlin.utils.GlideHelper
import android.wings.websarva.dokusyokannrijavatokotlin.utils.PostHelper
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions


class UsersPostAdapter(
    private val userInfo: UserInfoHelper,
    options: FirestoreRecyclerOptions<BookHelper>
) :
    FirestoreRecyclerAdapter<BookHelper, UsersPostAdapter.ViewHolder>(options) {

    lateinit var commentListener: OnCommentClickListener

    lateinit var bookListener: OnBookClickListener

    inner class ViewHolder(val itemBinding: PostCellBinding) : RecyclerView.ViewHolder(itemBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding = PostCellBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: BookHelper) {

        // 本の情報のUI
        GlideHelper.viewUserImage(userInfo.userImageUrl, holder.itemBinding.userImage)
        holder.itemBinding.userName.text = userInfo.userName
        holder.itemBinding.userReadDate.text = DateHelper.formatDate(model.createdAt)
        holder.itemBinding.userActionPlan.text = model.action
        GlideHelper.viewBookImage(model.imageUrl, holder.itemBinding.userBookImage)
        holder.itemBinding.userBookTitle.text = model.title
        holder.itemBinding.userAuthorName.text = model.author
        holder.itemBinding.bookLayout.setOnClickListener {
            bookListener.onBookClickListener(model)
        }

        // いいねとコメントのUI実装
        holder.itemBinding.favoriteCount.text = model.likedList.size.toString()
        holder.itemBinding.favoriteImage.setImageDrawable(PostHelper.getLikedDrawable(model.likedList))
        holder.itemBinding.favoriteImage.setOnClickListener {
            PostHelper.pushFavorite(model)
        }
        holder.itemBinding.commentCount.text = model.commentedList.size.toString()
        holder.itemBinding.commentImage.setImageDrawable(PostHelper.getCommentDrwable(model.commentedList))
        holder.itemBinding.commentImage.setOnClickListener {
            commentListener.onCommentClickListener(userInfo, model)
        }

    }

    fun setCommentClickListener(listener: OnCommentClickListener) {
        this.commentListener = listener
    }

    fun setBookClickListener(listener: OnBookClickListener) {
        this.bookListener = listener
    }
}