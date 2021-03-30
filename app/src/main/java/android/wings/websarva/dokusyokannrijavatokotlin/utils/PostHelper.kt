package android.wings.websarva.dokusyokannrijavatokotlin.utils

import android.graphics.drawable.Drawable
import android.wings.websarva.dokusyokannrijavatokotlin.MyApplication
import android.wings.websarva.dokusyokannrijavatokotlin.R
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.AuthHelper
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.FireStoreHelper
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.model.CommentHelper
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.model.BookHelper
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.model.LikeHelper
import androidx.core.content.ContextCompat

object PostHelper {
    /**
     * いいねの数を保存するメソッド
     * @param likedList ユーザーが既にいいねを押しているか
     * @param　model firestoreに保存するためのデータ
     */
    fun pushFavorite(model: BookHelper) {
        if (model.likedList.any { it.likedUserUid == AuthHelper.getUid() }) {
            model.likedList.removeAll { it.likedUserUid == AuthHelper.getUid() }
            model.likedCount = model.likedList.size
        } else {
            model.likedList.add(LikeHelper())
            model.likedCount = model.likedList.size
        }
        FireStoreHelper.saveBook(model)
    }

    /**
     * ユーザーのいいねによって画像を返す
     * @param likedList いいねをしたユーザーのリスト
     * @return いいねの画像
     */
    fun getLikedDrawable(likedList: List<LikeHelper>): Drawable? {
        return if (likedList.any {it.likedUserUid == AuthHelper.getUid()}) {
             ContextCompat.getDrawable(MyApplication.getAppContext(), R.drawable.ic_like)
        } else {
            ContextCompat.getDrawable(MyApplication.getAppContext(), R.drawable.ic_no_like)
        }
    }

    /**
     * ユーザーがコメントによって画像を返す
     * @param commentedList コメントしたユーザーのリスト
     * @return コメント画像
     */
    fun getCommentDrwable(commentedList: List<CommentHelper>): Drawable? {
        return if (commentedList.any { it.commentedUserUid == AuthHelper.getUid() }) {
            ContextCompat.getDrawable(MyApplication.getAppContext(), R.drawable.ic_comment)
        } else {
            ContextCompat.getDrawable(MyApplication.getAppContext(), R.drawable.ic_no_comment)
        }
     }

}