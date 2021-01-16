package android.wings.websarva.dokusyokannrijavatokotlin.realm.config

import io.realm.RealmConfiguration

object RealmConfigObject {
    val bookListConfig: RealmConfiguration = RealmConfiguration.Builder()
        .name("bookList")
        .build()

    val graphConfig: RealmConfiguration = RealmConfiguration.Builder()
        .name("graphData")
        .build()
}