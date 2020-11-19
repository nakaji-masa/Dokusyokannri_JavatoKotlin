package android.wings.websarva.dokusyokannrijavatokotlin.realm.config

import io.realm.RealmConfiguration

object RealmConfigObject {
    val bookListConfig = RealmConfiguration.Builder()
        .name("bookList")
        .build()

    val graphConfig = RealmConfiguration.Builder()
        .name("graphData")
        .build()
}