package android.wings.websarva.dokusyokannrijavatokotlin.firebase.model

import android.wings.websarva.dokusyokannrijavatokotlin.firebase.AuthHelper
import java.util.*
import kotlin.collections.HashMap

data class BookHelper(
    val docId: String = "",
    val uid: String = AuthHelper.getUid(),
    val title: String = "",
    val author: String = "",
    val action: String = "",
    val imageUrl: String = "",
    var likedCount: Int = 0,
    val likedList: MutableList<LikeHelper> = mutableListOf(),
    var commentedCount: Int = 0,
    val commentedList: MutableList<CommentHelper> = mutableListOf(),
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)
