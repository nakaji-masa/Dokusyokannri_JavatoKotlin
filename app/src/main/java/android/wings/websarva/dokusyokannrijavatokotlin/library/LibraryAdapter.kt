package android.wings.websarva.dokusyokannrijavatokotlin.library



import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.wings.websarva.dokusyokannrijavatokotlin.interfaces.OnCommentClickListener
import android.wings.websarva.dokusyokannrijavatokotlin.R
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.FireStoreHelper
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.model.BookHelper
import android.wings.websarva.dokusyokannrijavatokotlin.interfaces.OnUserImageClickListener
import android.wings.websarva.dokusyokannrijavatokotlin.utils.*
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.gson.Gson
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class LibraryAdapter(options: FirestoreRecyclerOptions<BookHelper>) :
    FirestoreRecyclerAdapter<BookHelper, LibraryAdapter.ViewHolder>(options) {

    private lateinit var commentListener: OnCommentClickListener
    private lateinit var userImageListener: OnUserImageClickListener

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

                    val userJson = Gson().toJson(userInfo)
                    holder.userImage.setOnClickListener {
                        userImageListener.onUserImageClickListener(model.uid, userJson)
                    }
                    holder.commentImage.setOnClickListener {
                        val bookJson = Gson().toJson(model)
                        commentListener.onCommentClickListener(userJson, bookJson)
                    }

                }
                handler.post(runnable)
            }
        }

        // ユーザーが「いいね」と「コメント」をしているか？
        val liked = PostHelper.hasLiked(model.likedUserList)
        val commented = PostHelper.hasCommented(model.commentList)

        // likedの結果によって、表示する画像を決める。
        val favoriteDrawable = PostHelper.getFabDrawable(context, liked)
        val commentDrawable = PostHelper.getCommentDrawable(context, commented)

        holder.date.text = model.date
        holder.actionPlan.text = model.action
        GlideHelper.viewBookImage(model.imageUrl, holder.bookImage)
        holder.bookTitle.text = model.title
        holder.bookAuthor.text = model.author
        holder.favoriteImage.setImageDrawable(favoriteDrawable)
        holder.favoriteCount.text = model.likedUserList.size.toString()
        holder.favoriteImage.setOnClickListener {
            PostHelper.pushFavorite(liked, model)
        }
        holder.commentImage.setImageDrawable(commentDrawable)
        holder.commentCount.text = model.commentList.size.toString()
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

