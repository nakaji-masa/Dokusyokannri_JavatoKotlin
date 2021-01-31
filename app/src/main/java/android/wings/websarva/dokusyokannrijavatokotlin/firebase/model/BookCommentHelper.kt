package android.wings.websarva.dokusyokannrijavatokotlin.firebase.model

import android.wings.websarva.dokusyokannrijavatokotlin.utils.DateHelper

data class BookCommentHelper(
    val userUid: String = "",
    val comment: String = "",
    val date: String = DateHelper.getToday()
)
