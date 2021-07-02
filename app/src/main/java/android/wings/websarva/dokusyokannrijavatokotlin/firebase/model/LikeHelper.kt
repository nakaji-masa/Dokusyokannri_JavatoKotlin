package android.wings.websarva.dokusyokannrijavatokotlin.firebase.model

import android.wings.websarva.dokusyokannrijavatokotlin.firebase.AuthHelper
import java.util.*

data class LikeHelper(
    val id: String = UUID.randomUUID().toString(),
    val likedUserUid: String = AuthHelper.getUid(),
    var checked: Boolean = false,
    val date: Date = Date()
)