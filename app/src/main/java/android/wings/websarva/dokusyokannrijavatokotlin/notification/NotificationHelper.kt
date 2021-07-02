package android.wings.websarva.dokusyokannrijavatokotlin.notification

import android.wings.websarva.dokusyokannrijavatokotlin.firebase.AuthHelper
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.FireStoreHelper
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.model.BookHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object NotificationHelper {

    private var notificationList: MutableList<Notification> = mutableListOf()
    const val TYPE_LIKE = "type_like"
    private const val TYPE_COMMENT = "type_comment"

    /**
     * フィールドのnotificationListをセットするメソッド
     */
    fun setNotificationList(notificationList: MutableList<Notification>) {
        this.notificationList = notificationList
    }

    /**
     * フィールドのnotificationListを返すメソッド
     */
    fun getNotificationList(): MutableList<Notification> {
        notificationList.sortByDescending { it.timeStamp }
        return notificationList
    }

    /**
     * notificationListを作成するメソッド
     */
    suspend fun createNotificationList() {
        // いいねをされている投稿を取得
        var postList = FireStoreHelper.getBookDataListOnlyLiked()

        // notificationListに格納
        addNotificationFromLiked(postList)

        // コメントされいる投稿を取得
        withContext(Dispatchers.IO) {
            postList = FireStoreHelper.getBookDataListOnlyCommented()
        }

        // notificationList格納
        addNotificationFromCommented(postList)
    }

    /**
     * 通知データをリストにセットする
     * @param postList fireStoreから取得した投稿データ(いいねをされたもの)
     */
    private fun addNotificationFromLiked(postList: List<BookHelper>) {
        // 通知のデータを追加する
        for (post in postList) {
            for (liked in post.likedList) {
                if (liked.likedUserUid != AuthHelper.getUid()) {
                    notificationList.add(
                        Notification(
                            id = liked.id,
                            content = post.action,
                            type = TYPE_LIKE,
                            timeStamp = liked.date,
                            bookHelper = post,
                            likeHelper = liked
                        )
                    )
                }
            }
        }
    }

    /**
     * 通知データをリストにセットする
     * @param postList fireStoreから取得した投稿データ(コメントされたもの)
     */
    private fun addNotificationFromCommented(postList: List<BookHelper>) {
        // 通知のデータを追加する
        for (post in postList) {
            for (commented in post.commentedList) {
                notificationList.add(
                    Notification(
                        id = commented.id,
                        content = post.action,
                        type = TYPE_COMMENT,
                        timeStamp = commented.date,
                        bookHelper = post,
                        commentHelper = commented
                    )
                )
            }
        }
    }

    /**
     * ユーザーが確認していない通知の数を返す
     * @return 確認していない通知の数
     */
    fun getNotCheckedNotificationCount(): Int {
        // 戻り値
        var result = 0
        for (notification in notificationList) {
            if (notification.type == TYPE_LIKE) {
                if (!notification.likeHelper.checked) {
                    result += 1
                }
            } else {
                if (!notification.commentHelper.checked) {
                    result += 1
                }
            }
        }
        return result
    }
}