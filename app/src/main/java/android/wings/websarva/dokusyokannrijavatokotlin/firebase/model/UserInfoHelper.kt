package android.wings.websarva.dokusyokannrijavatokotlin.firebase.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserInfoHelper(
    var uid: String = "",
    val userName: String = "",
    val introduction: String = "",
    val userImageUrl: String = ""
): Parcelable