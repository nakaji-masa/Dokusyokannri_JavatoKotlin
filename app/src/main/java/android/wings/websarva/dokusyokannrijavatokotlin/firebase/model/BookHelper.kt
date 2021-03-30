package android.wings.websarva.dokusyokannrijavatokotlin.firebase.model

import android.os.Parcelable
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.AuthHelper
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import java.util.*

@Parcelize
data class BookHelper(
    val docId: String = "",
    val uid: String = AuthHelper.getUid(),
    var title: String = "",
    var author: String = "",
    var action: String = "",
    var imageUrl: String = "",
    var likedCount: Int = 0,
    val likedList: @RawValue MutableList<LikeHelper> = mutableListOf(),
    var commentedCount: Int = 0,
    val commentedList: @RawValue MutableList<CommentHelper> = mutableListOf(),
    val createdAt: Date = Date(),
    var updatedAt: Date = Date()
): Parcelable
