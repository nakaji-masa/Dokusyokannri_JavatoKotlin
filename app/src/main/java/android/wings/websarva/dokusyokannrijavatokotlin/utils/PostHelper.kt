package android.wings.websarva.dokusyokannrijavatokotlin.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.wings.websarva.dokusyokannrijavatokotlin.R
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.AuthHelper
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.FireStoreHelper
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.model.CommentHelper
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.model.BookHelper
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.model.LikeHelper
import androidx.core.content.ContextCompat
import java.util.*
import kotlin.collections.HashMap

object PostHelper {
    /**
     * いいねの数を保存するメソッド
     * @param liked ユーザーが既にいいねを押しているか
     * @param　firestoreに保存するためのデータ
     */
    fun pushFavorite(liked: Boolean, model: BookHelper) {
        if (liked) {
            model.likedList.removeAll { it.likedUserUid == AuthHelper.getUid() }
            model.likedCount = model.likedList.size
        } else {
            model.likedList.add(LikeHelper())
            model.likedCount = model.likedList.size
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
     * @param likedList いいねをしたユーザーのリスト
     * @return いいねをしているかの判定結果
     */
    fun hasLiked(likedList: List<LikeHelper>): Boolean {
        return likedList.any { it.likedUserUid == AuthHelper.getUid() }
    }

    /**
     * ユーザーがコメントしているか判定するメソッド
     * @param commentedList コメントしたユーザーのリスト
     * @return 判定結果
     */
    fun hasCommented(commentedList: List<CommentHelper>): Boolean {
        return commentedList.any { it.commentedUserUid == AuthHelper.getUid() }
    }

}