package android.wings.websarva.dokusyokannrijavatokotlin.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.wings.websarva.dokusyokannrijavatokotlin.R
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.AuthHelper
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.FireStoreHelper
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.model.BookCommentHelper
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.model.BookHelper
import androidx.core.content.ContextCompat

object PostHelper {
    /**
     * いいねの数を保存するメソッド
     * @param liked ユーザーが既にいいねを押しているか
     * @param　firestoreに保存するためのデータ
     */
    fun pushFavorite(liked: Boolean, model: BookHelper) {
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
    fun getFabDrawable(context: Context, liked: Boolean): Drawable? {
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
    fun getCommentDrawable(context: Context, commented: Boolean): Drawable? {
        return if (commented) {
            ContextCompat.getDrawable(context, R.drawable.ic_comment)
        } else {
            ContextCompat.getDrawable(context, R.drawable.ic_no_comment)
        }
    }

    /**
     * ユーザーが投稿に対して、いいねをしたか判定するメソッド
     * @param likedUserList いいねをしたユーザーのリスト
     * @return いいねをしているかの判定結果
     */
    fun hasLiked(likedUserList: List<String>): Boolean {
        return likedUserList.contains(AuthHelper.getUid())
    }

    /**
     * ユーザーがコメントしているか判定するメソッド
     * @param commentedList コメントしたユーザーのリスト
     * @return 判定結果
     */
    fun hasCommented(commentedList: List<BookCommentHelper>): Boolean {
        return commentedList.any { it.userUid == AuthHelper.getUid() }
    }

}