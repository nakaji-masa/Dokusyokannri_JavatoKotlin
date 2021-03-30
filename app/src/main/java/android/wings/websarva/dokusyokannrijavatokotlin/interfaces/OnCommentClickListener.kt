package android.wings.websarva.dokusyokannrijavatokotlin.interfaces

import android.wings.websarva.dokusyokannrijavatokotlin.firebase.model.BookHelper
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.model.UserInfoHelper

interface OnCommentClickListener {
    fun onCommentClickListener(user: UserInfoHelper, book: BookHelper)
}