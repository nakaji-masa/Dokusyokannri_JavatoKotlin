package android.wings.websarva.dokusyokannrijavatokotlin.firebase.model

import android.wings.websarva.dokusyokannrijavatokotlin.firebase.AuthHelper
import java.util.*

data class CommentHelper(
    val id: String = UUID.randomUUID().toString(),
    val commentedUserUid: String = AuthHelper.getUid(),
    val comment: String = "",
    var checked: Boolean = false,
    val date: Date = Date()
)
