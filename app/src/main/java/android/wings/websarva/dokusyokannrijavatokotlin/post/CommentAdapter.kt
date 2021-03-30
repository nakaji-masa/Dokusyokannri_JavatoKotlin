package android.wings.websarva.dokusyokannrijavatokotlin.post


import android.view.LayoutInflater
import android.view.ViewGroup
import android.wings.websarva.dokusyokannrijavatokotlin.databinding.CommentCellBinding
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.model.CommentHelper
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.FireStoreHelper
import androidx.recyclerview.widget.RecyclerView
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.model.UserInfoHelper
import android.wings.websarva.dokusyokannrijavatokotlin.interfaces.OnUserImageClickListener
import android.wings.websarva.dokusyokannrijavatokotlin.utils.DateHelper
import android.wings.websarva.dokusyokannrijavatokotlin.utils.GlideHelper

class CommentAdapter(private val commentList: List<CommentHelper>) :
    RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    private lateinit var listener: OnUserImageClickListener

    inner class CommentViewHolder(val itemBinding: CommentCellBinding) : RecyclerView.ViewHolder(itemBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val itemBinding = CommentCellBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommentViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        FireStoreHelper.getUserRef(commentList[position].commentedUserUid).addOnSuccessListener { documentSnapshot ->
            val userInfo = documentSnapshot.toObject(UserInfoHelper::class.java)
            userInfo?.let { userData ->
                holder.itemBinding.commentUserName.text = userData.userName
                GlideHelper.viewUserImage(userData.userImageUrl, holder.itemBinding.commentUserImage)
                holder.itemBinding.commentUserImage.setOnClickListener {
                    listener.onUserImageClickListener(userData)
                }
                holder.itemBinding.commentText.text = commentList[position].comment
                holder.itemBinding.commentDate.text = DateHelper.formatDate(commentList[position].date)
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