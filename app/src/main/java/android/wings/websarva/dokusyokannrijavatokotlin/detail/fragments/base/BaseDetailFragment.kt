package android.wings.websarva.dokusyokannrijavatokotlin.detail.fragments.base

import android.os.Bundle
import android.wings.websarva.dokusyokannrijavatokotlin.detail.activities.DetailActivity
import android.wings.websarva.dokusyokannrijavatokotlin.realm.`object`.BookObject
import android.wings.websarva.dokusyokannrijavatokotlin.realm.manager.RealmManager
import androidx.fragment.app.Fragment
import io.realm.Realm
import io.realm.kotlin.where

open class BaseDetailFragment :Fragment() {
    lateinit var realm: Realm
    var bookId: String? = null
    var bookObj: BookObject? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bookId = requireActivity().intent.getStringExtra(DetailActivity.BOOK_ID)
        realm = RealmManager.getBookRealmInstance()
        bookObj =realm.where<BookObject>().equalTo("id", bookId).findFirst()
    }

}