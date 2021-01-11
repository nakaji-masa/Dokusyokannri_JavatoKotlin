package android.wings.websarva.dokusyokannrijavatokotlin.library

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.wings.websarva.dokusyokannrijavatokotlin.R
import android.wings.websarva.dokusyokannrijavatokotlin.register.BookCommentHelper
import android.wings.websarva.dokusyokannrijavatokotlin.utils.FireStorageHelper
import android.wings.websarva.dokusyokannrijavatokotlin.utils.FireStoreHelper
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import android.os.Handler
import android.wings.websarva.dokusyokannrijavatokotlin.utils.GlideHelper
import com.bumptech.glide.Glide

class CommentAdapter(private val commentList: List<BookCommentHelper>) :
    RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {
    class CommentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userImage: ImageView = view.findViewById(R.id.commentUserImage)
        val userName: TextView = view.findViewById(R.id.commentUserName)
        val date: TextView = view.findViewById(R.id.commentDate)
        val comment: TextView = view.findViewById(R.id.commentText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.comment_cell, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val handler = Handler()
        GlobalScope.launch {
            val userInfo = FireStoreHelper.getUserData(commentList[position].userUid)
            if (userInfo != null) {
                val byteArray = FireStorageHelper.getImageByteArray(userInfo.imageRef)
                val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                handler.post {
                    holder.userName.text = userInfo.userName
                    GlideHelper.viewUserImage(bitmap, holder.userImage)
                    holder.comment.text = commentList[position].comment
                    holder.date.text = commentList[position].date
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return commentList.size
    }
}