package android.wings.websarva.dokusyokannrijavatokotlin.library


import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.wings.websarva.dokusyokannrijavatokotlin.R
import android.wings.websarva.dokusyokannrijavatokotlin.library.activities.PostDetailActivity
import android.wings.websarva.dokusyokannrijavatokotlin.register.BookHelper
import android.wings.websarva.dokusyokannrijavatokotlin.register.LoadedUserInfo
import android.wings.websarva.dokusyokannrijavatokotlin.register.ModelHelper
import android.wings.websarva.dokusyokannrijavatokotlin.utils.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class LibraryAdapter(options: FirestoreRecyclerOptions<BookHelper>) :
    FirestoreRecyclerAdapter<BookHelper, LibraryAdapter.ViewHolder>(options) {

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

                // 取得できれば、FireStorageから画像を取得する。
                val byteArray = FireStorageHelper.getImageByteArray(userInfo.imageRef)
                val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)

                // ユーザーが「いいね」と「コメント」をしているか？
                var liked = model.likedUserList.contains(AuthHelper.getUid())
                val commented = model.commentList.any { it.userUid == AuthHelper.getUid() }

                // likedの結果によって、表示する画像を決める。
                val favoriteDrawable = if (liked) {
                    ContextCompat.getDrawable(context, R.drawable.ic_like)
                } else {
                    ContextCompat.getDrawable(context, R.drawable.ic_no_like)
                }

                val commentDrawable = if (commented) {
                    ContextCompat.getDrawable(context, R.drawable.ic_comment)
                } else {
                    ContextCompat.getDrawable(context, R.drawable.ic_no_comment)
                }

                // UI実行
                val runnable = Runnable {
                    holder.userName.text = userInfo.userName
                    GlideHelper.viewUserImage(bitmap, holder.userImage)
                    holder.date.text = model.date
                    holder.actionPlan.text = model.action
                    Glide.with(context).load(model.imageUrl).into(holder.bookImage)
                    holder.bookTitle.text = model.title
                    holder.bookAuthor.text = model.author
                    holder.favoriteImage.setImageDrawable(favoriteDrawable)
                    holder.favoriteCount.text = model.likedUserList.size.toString()
                    holder.favoriteImage.setOnClickListener {
                        pushFavorite(liked, model, position)
                        liked = !liked
                    }
                    holder.commentImage.setImageDrawable(commentDrawable)
                    holder.commentCount.text = model.commentList.size.toString()
                    holder.commentImage.setOnClickListener {
                        pushComment(context, model, LoadedUserInfo(userInfo.userName, bitmap))
                    }
                }
                handler.post(runnable)
            }
        }
    }

    // 「いいね」を押したときの処理
    private fun pushFavorite(liked: Boolean, model: BookHelper, position: Int) {
        if (liked) {
            model.likedUserList.remove(AuthHelper.getUid())
        } else {
            model.likedUserList.add(AuthHelper.getUid())
        }
        FireStoreHelper.savePostData(model)
        notifyItemChanged(position)
    }

    private fun pushComment(
        context: Context,
        bookData: BookHelper,
        loadedUserData: LoadedUserInfo
    ) {
        ModelHelper.setData(bookData, loadedUserData)
        val intent = Intent(context, PostDetailActivity::class.java)
        context.startActivity(
            intent
        )
    }

}

