package android.wings.websarva.dokusyokannrijavatokotlin.post


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.wings.websarva.dokusyokannrijavatokotlin.R
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.model.CommentHelper
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.FireStoreHelper
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import android.os.Handler
import android.wings.websarva.dokusyokannrijavatokotlin.interfaces.OnUserImageClickListener
import android.wings.websarva.dokusyokannrijavatokotlin.utils.DateHelper
import android.wings.websarva.dokusyokannrijavatokotlin.utils.GlideHelper
import com.google.gson.Gson

class CommentAdapter(private val commentList: List<CommentHelper>) :
    RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    private lateinit var listener: OnUserImageClickListener

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
            val uid = commentList[position].commentedUserUid
            val userInfo = FireStoreHelper.getUserInfoFromUid(uid)

            if (userInfo != null) {
                handler.post {
                    holder.userName.text = userInfo.userName
                    GlideHelper.viewUserImage(userInfo.userImageUrl, holder.userImage)
                    holder.userImage.setOnClickListener {
                        val userJson = Gson().toJson(userInfo)
                        listener.onUserImageClickListener(userJson)
                    }
                    holder.comment.text = commentList[position].comment
                    holder.date.text = DateHelper.formatDate(commentList[position].date)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return commentList.size
    }

    fun setUserImageClickListener(listener: OnUserImageClickListener) {
        this.listener = listener
    }
}