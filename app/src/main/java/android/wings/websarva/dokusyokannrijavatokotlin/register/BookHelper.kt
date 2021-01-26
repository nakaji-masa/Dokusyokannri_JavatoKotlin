package android.wings.websarva.dokusyokannrijavatokotlin.register

import java.util.*

data class BookHelper(
    val docId: String = "",
    val date: String = "",
    val title: String = "",
    val action: String = "",
    val author: String = "",
    val imageUrl: String = "",
    val uid: String = "",
    val likedUserList: MutableList<String> = mutableListOf(),
    val commentList: MutableList<BookCommentHelper> = mutableListOf(),
    val createdAt: Date = Date(),
    val updateAt: Date = Date()
)
