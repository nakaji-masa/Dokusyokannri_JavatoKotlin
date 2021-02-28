package android.wings.websarva.dokusyokannrijavatokotlin.notification

import android.content.Context
import android.os.Handler
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.wings.websarva.dokusyokannrijavatokotlin.R
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.FireStoreHelper
import android.wings.websarva.dokusyokannrijavatokotlin.utils.GlideHelper
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class NotificationsAdapter(
    private val context: Context,
    private val notificationList: MutableList<Notification>
) : RecyclerView.Adapter<NotificationsAdapter.ViewHolder>() {

    lateinit var listener: OnNotificationClickListener

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.findViewById(R.id.icon)
        val userImage: ImageView = view.findViewById(R.id.userImage)
        val notificationTextView: TextView = view.findViewById(R.id.notificationTextView)
        val postContentTextView: TextView = view.findViewById(R.id.postContent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.notification_cell, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notification = notificationList[position]
        val handler = Handler()

        GlobalScope.launch {

            // ユーザー情報を取得
            val userInfo = FireStoreHelper.getUserInfoFromUid(
                if (notification.type == NotificationHelper.TYPE_LIKE) {
                    notification.likeHelper.likedUserUid
                } else {
                    notification.commentHelper.commentedUserUid
                }
            )

            // UI実装
            handler.post {
                if (notification.type == NotificationHelper.TYPE_LIKE) {
                    holder.icon.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.ic_like
                        )
                    )
                    // getStringを呼び出すとHTMLタグを削除してしまう。
                    val text = context.getString(
                        R.string.notification_user_text,
                        userInfo?.userName.toString(),
                        "いいね"
                    )

                    // スタイルの復元
                    val styledText = Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY)
                    holder.notificationTextView.text = styledText

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
                    holder.icon.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.ic_comment
                        )
                    )

                    // テキストの設定
                    val text = context.getString(
                        R.string.notification_user_text,
                        userInfo?.userName.toString(),
                        "コメント"
                    )
                    val styledText = Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY)
                    holder.notificationTextView.text = styledText

                    // 背景の設定
                    if (notification.commentHelper.checked) {
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
                }

                // いいねかコメントしたユーザーの画像
                GlideHelper.viewUserImage(userInfo?.userImageUrl, holder.userImage)

                // 投稿の内容
                holder.postContentTextView.text = notification.content

                // リスナーの設定
                holder.itemView.setOnClickListener {
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