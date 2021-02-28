package android.wings.websarva.dokusyokannrijavatokotlin

import android.app.Application
import android.content.Context
import android.wings.websarva.dokusyokannrijavatokotlin.realm.manager.RealmManager
import io.realm.Realm
import io.realm.RealmConfiguration

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        context = applicationContext

        //Realmの初期化
        Realm.init(this)

        //設定をセット
        Realm.setDefaultConfiguration(RealmManager.bookConfig)
        Realm.setDefaultConfiguration(RealmManager.graphConfig)
        Realm.setDefaultConfiguration(RealmManager.userConfig)
    }

    companion object {
        private lateinit var context: Context

        /**
         * contextを返すメソッド
         * @return Context
         */
        fun getAppContext() : Context {
            return context
        }
    }
}