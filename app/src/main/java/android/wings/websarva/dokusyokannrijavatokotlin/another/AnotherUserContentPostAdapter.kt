package android.wings.websarva.dokusyokannrijavatokotlin.another

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.wings.websarva.dokusyokannrijavatokotlin.R
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.model.BookHelper
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.model.UserInfoHelper
import android.wings.websarva.dokusyokannrijavatokotlin.interfaces.OnCommentClickListener
import android.wings.websarva.dokusyokannrijavatokotlin.utils.DateHelper
import android.wings.websarva.dokusyokannrijavatokotlin.utils.GlideHelper
import android.wings.websarva.dokusyokannrijavatokotlin.utils.PostHelper
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.gson.Gson


class AnotherUserContentPostAdapter(
    private val userInfo: UserInfoHelper,
    options: FirestoreRecyclerOptions<BookHelper>
) :
    FirestoreRecyclerAdapter<BookHelper, AnotherUserContentPostAdapter.ViewHolder>(options) {

    lateinit var listener: OnCommentClickListener

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userImage: ImageView = view.findViewById(R.id.userImage)
        val userName: TextView = view.findViewById(R.id.userName)
        val date: TextView = view.findViewById(R.id.userReadDate)
        val actionPlan: TextView = view.findViewById(R.id.userActionPlan)
        val bookImage: ImageView = view.findViewById(R.id.userBookImage)
        val bookTitle: TextView = view.findViewById(R.id.userBookTitle)
        val bookAuthor: TextView = view.findViewById(R.id.userAuthorName)
        val favoriteImage: ImageView = view.findViewById(R.id.favoriteImage)
        val favoriteCount: TextView = view.findViewById(R.id.favoriteCount)
        val commentImage: ImageView = view.findViewById(R.id.commentImage)
        val commentCount: TextView = view.findViewById(R.id.commentCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.post_cell, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: BookHelper) {
        // ユーザーが「いいね」と「コメント」をしているか？
        val liked = PostHelper.hasLiked(model.likedList)
        val commented = PostHelper.hasCommented(model.commentedList)

        GlideHelper.viewUserImage(userInfo.userImageUrl, holder.userImage)
        holder.userName.text = userInfo.userName
        holder.date.text = DateHelper.formatDate(model.createdAt)
        holder.actionPlan.text = model.action
        GlideHelper.viewBookImage(model.imageUrl, holder.bookImage)
        holder.bookTitle.text = model.title
        holder.bookAuthor.text = model.author

        // drawableの取得
        val context = holder.itemView.context
        val favoriteDrawable = PostHelper.getFabDrawable(context, liked)
        val commentDrawable = PostHelper.getCommentDrawable(context, commented)

        // いいねとコメントのUI実装
        holder.favoriteCount.text = model.likedList.size.toString()
        holder.favoriteImage.setImageDrawable(favoriteDrawable)
        holder.favoriteImage.setOnClickListener {
            PostHelper.pushFavorite(liked, model)
        }
        holder.commentCount.text = model.commentedList.size.toString()
        holder.commentImage.setImageDrawable(commentDrawable)
        holder.commentImage.setOnClickListener {
            val userJson = Gson().toJson(userInfo)
            val bookJson = Gson().toJson(model)
            listener.onCommentClickListener(userJson, bookJson)
        }

    }

    fun setCommentClickListener(listener: OnCommentClickListener) {
        this.listener = listener
    }
}