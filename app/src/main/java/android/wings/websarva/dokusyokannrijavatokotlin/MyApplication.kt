package android.wings.websarva.dokusyokannrijavatokotlin

import android.app.Application
import android.content.Context
import io.realm.Realm
import io.realm.RealmConfiguration

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        context = applicationContext

        //Realmの初期化
        Realm.init(this)

        val bookListConfig = RealmConfiguration.Builder()
            .name("bookList")
            .build()

        val graphConfig = RealmConfiguration.Builder()
            .name("graphData")
            .build()

        //設定をセット
        Realm.setDefaultConfiguration(bookListConfig)

        Realm.setDefaultConfiguration(graphConfig)
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