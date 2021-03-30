package android.wings.websarva.dokusyokannrijavatokotlin.interfaces

import android.wings.websarva.dokusyokannrijavatokotlin.firebase.model.BookHelper

interface OnBookClickListener {
    fun onBookClickListener(book: BookHelper)
}