package android.wings.websarva.dokusyokannrijavatokotlin

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

    //Realmの初期化
        Realm.init(this)

        //Realmデータベースの設定
        val config = RealmConfiguration.Builder()
            .name("bookList")
            .build()

        //設定をセット
        Realm.setDefaultConfiguration(config)

    }
}