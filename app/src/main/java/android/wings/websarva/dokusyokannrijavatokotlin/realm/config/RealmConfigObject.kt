package android.wings.websarva.dokusyokannrijavatokotlin.realm.config

import io.realm.RealmConfiguration

object RealmConfigObject {
    val bookConfig: RealmConfiguration = RealmConfiguration.Builder()
        .name("book")
        .build()

    val graphConfig: RealmConfiguration = RealmConfiguration.Builder()
        .name("graph")
        .build()

    val userConfig: RealmConfiguration = RealmConfiguration.Builder()
        .name("userInfo")
        .build()
}