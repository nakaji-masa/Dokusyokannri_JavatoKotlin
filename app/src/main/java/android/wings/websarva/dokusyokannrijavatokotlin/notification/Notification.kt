package android.wings.websarva.dokusyokannrijavatokotlin.notification

import android.wings.websarva.dokusyokannrijavatokotlin.firebase.model.BookHelper
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.model.CommentHelper
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.model.LikeHelper
import java.util.*

data class Notification(
    val id: String = "",
    val content: String = "",
    val type: String = "",
    val timeStamp: Date,
    val bookHelper: BookHelper = BookHelper(),
    val likeHelper: LikeHelper = LikeHelper(),
    val commentHelper: CommentHelper = CommentHelper()
)