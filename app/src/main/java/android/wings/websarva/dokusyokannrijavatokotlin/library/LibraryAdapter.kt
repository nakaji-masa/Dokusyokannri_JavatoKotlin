package android.wings.websarva.dokusyokannrijavatokotlin.library


import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.wings.websarva.dokusyokannrijavatokotlin.R
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.AuthHelper
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.FireStoreHelper
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.model.BookHelper
import android.wings.websarva.dokusyokannrijavatokotlin.utils.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.gson.Gson
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class LibraryAdapter(options: FirestoreRecyclerOptions<BookHelper>) :
    FirestoreRecyclerAdapter<BookHelper, LibraryAdapter.ViewHolder>(options) {

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
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.library_cell, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: BookHelper) {
        val handler = Handler()
        val context = holder.itemView.context

        GlobalScope.launch {
            // userコレクションからプロフィール情報を取得
            val userInfo = FireStoreHelper.getUserData(model.uid)

            if (userInfo != null) {
                // UI実行
                val runnable = Runnable {
                    holder.userName.text = userInfo.userName
                    GlideHelper.viewUserImage(userInfo.userImageUrl, holder.userImage)
                    holder.commentImage.setOnClickListener {
                        val userJson = Gson().toJson(userInfo)
                        val bookJson = Gson().toJson(model)
                        listener.onCommentClickListener(userJson, bookJson)
                    }
                }
                handler.post(runnable)
            }
        }

        // ユーザーが「いいね」と「コメント」をしているか？
        val liked = model.likedUserList.contains(AuthHelper.getUid())
        val commented = model.commentList.any { it.userUid == AuthHelper.getUid() }

        // likedの結果によって、表示する画像を決める。
        val favoriteDrawable = getFabDrawable(context, liked)
        val commentDrawable = getCommentDrawable(context, commented)

        holder.date.text = model.date
        holder.actionPlan.text = model.action
        GlideHelper.viewBookImage(model.imageUrl, holder.bookImage)
        holder.bookTitle.text = model.title
        holder.bookAuthor.text = model.author
        holder.favoriteImage.setImageDrawable(favoriteDrawable)
        holder.favoriteCount.text = model.likedUserList.size.toString()
        holder.favoriteImage.setOnClickListener {
            pushFavorite(liked, model)
        }
        holder.commentImage.setImageDrawable(commentDrawable)
        holder.commentCount.text = model.commentList.size.toString()
    }

    /**
     * いいねの数を保存するメソッド
     * @param liked ユーザーが既にいいねを押しているか
     * @param　firestoreに保存するためのデータ
     */
    private fun pushFavorite(liked: Boolean, model: BookHelper) {
        if (liked) {
            model.likedUserList.remove(AuthHelper.getUid())
        } else {
            model.likedUserList.add(AuthHelper.getUid())
        }
        FireStoreHelper.savePostData(model)
    }

    /**
     * ユーザーがいいねをしているかで、戻り値のdrawableを決める
     * @param context
     * @param liked
     * @return Drawable
     */
    private fun getFabDrawable(context: Context, liked: Boolean): Drawable? {
        return if (liked) {
            ContextCompat.getDrawable(context, R.drawable.ic_like)
        } else {
            ContextCompat.getDrawable(context, R.drawable.ic_no_like)
        }
    }

    /**
     * ユーザー投稿に対してコメントをしているかで、戻り値のDrawableを変える
     * @param context
     * @param commented
     * @return Drawable
     */
    private fun getCommentDrawable(context: Context, commented: Boolean): Drawable? {
        return if (commented) {
            ContextCompat.getDrawable(context, R.drawable.ic_comment)
        } else {
            ContextCompat.getDrawable(context, R.drawable.ic_no_comment)
        }
    }

    interface OnCommentClickListener {
        fun onCommentClickListener(userJson: String, bookJson: String)
    }

    /**
     * フィールドのlistenerをセットする
     * @param listener OnItemClickListener型のインタフェース
     */
    fun setItemClickListener(listener: OnCommentClickListener) {
        this.listener = listener
    }

}

