package android.wings.websarva.dokusyokannrijavatokotlin.notification

import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import android.wings.websarva.dokusyokannrijavatokotlin.R
import android.wings.websarva.dokusyokannrijavatokotlin.databinding.NotificationCellBinding
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.FireStoreHelper
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.model.UserInfoHelper
import android.wings.websarva.dokusyokannrijavatokotlin.utils.GlideHelper
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.flow

class NotificationsAdapter(
    private val context: Context,
    private val notificationList: MutableList<Notification>
) : RecyclerView.Adapter<NotificationsAdapter.ViewHolder>() {

    lateinit var listener: OnNotificationClickListener

    inner class ViewHolder(val itemBinding: NotificationCellBinding) : RecyclerView.ViewHolder(itemBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding = NotificationCellBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notification = notificationList[position]

        FireStoreHelper.getUserRef(
            if (notification.type == NotificationHelper.TYPE_LIKE) {
                notification.likeHelper.likedUserUid
            } else {
                notification.commentHelper.commentedUserUid
            }
        ).addOnSuccessListener {
            val userInfo = it.toObject(UserInfoHelper::class.java)
            userInfo?.let {
                if (notification.type == NotificationHelper.TYPE_LIKE) {
                    holder.itemBinding.icon.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.ic_like
                        )
                    )
                    // getStringを呼び出すとHTMLタグを削除してしまう。
                    val text = context.getString(
                        R.string.notification_user_text,
                        userInfo.userName,
                        "いいね"
                    )

                    // スタイルの復元
                    val styledText = Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY)
                    holder.itemBinding.notificationTextView.text = styledText

                    // 背景の設定
                    if (notification.likeHelper.checked) {
                        holder.itemView.background = ContextCompat.getDrawable(
                            context,
                            R.drawable.checked_notification_cell_bg
                        )
                    } else {
                        holder.itemView.background = ContextCompat.getDrawable(
                            context,
                            R.drawable.not_checked_notification_cell_bg
                        )
                    }

                } else {
                    // アイコン設定
                    holder.itemBinding.icon.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.ic_comment
                        )
                    )

                    // テキストの設定
                    val text = context.getString(
                        R.string.notification_user_text,
                        userInfo.userName,
                        "コメント"
                    )
                    val styledText = Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY)
                    holder.itemBinding.notificationTextView.text = styledText

                    // 背景の設定
                    if (notification.commentHelper.checked) {
                        holder.itemBinding.root.background = ContextCompat.getDrawable(
                            context,
                            R.drawable.checked_notification_cell_bg
                        )
                    } else {
                        holder.itemBinding.root.background = ContextCompat.getDrawable(
                            context,
                            R.drawable.not_checked_notification_cell_bg
                        )
                    }
                }

                // いいねかコメントしたユーザーの画像
                GlideHelper.viewUserImage(userInfo.userImageUrl, holder.itemBinding.userImage)

                // 投稿の内容
                holder.itemBinding.postContent.text = notification.content

                // リスナーの設定
                holder.itemBinding.root.setOnClickListener {
                    if (notification.type == NotificationHelper.TYPE_LIKE) {
                        notification.likeHelper.checked = true
                    } else {
                        notification.commentHelper.checked = true
                    }
                    notifyItemChanged(position)
                    NotificationHelper.setNotificationList(notificationList)
                    listener.onNotificationClickListener(notification)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return notificationList.size
    }

    interface OnNotificationClickListener {
        fun onNotificationClickListener(notification: Notification)
    }

    fun setOnNotificationClickListener(listener: OnNotificationClickListener) {
        this.listener = listener
    }

}