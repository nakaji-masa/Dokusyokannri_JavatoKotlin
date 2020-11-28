package android.wings.websarva.dokusyokannrijavatokotlin.register

import android.net.UrlQuerySanitizer
import com.google.firebase.firestore.Blob

data class BookHelper(
    var title: String,
    var date: String,
    var notice: String,
    var action: String,
    var image: String
) {
    constructor() : this("", "", "", "", "")
}