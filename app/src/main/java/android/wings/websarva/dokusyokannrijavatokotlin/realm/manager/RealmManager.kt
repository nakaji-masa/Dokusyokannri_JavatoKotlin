package android.wings.websarva.dokusyokannrijavatokotlin.realm.manager

import io.realm.Realm
import io.realm.RealmConfiguration

object RealmManager {

    // config
    val bookConfig: RealmConfiguration = RealmConfiguration.Builder()
        .name("book")
        .build()

    val graphConfig: RealmConfiguration = RealmConfiguration.Builder()
        .name("graph")
        .build()

    val userConfig: RealmConfiguration = RealmConfiguration.Builder()
        .name("userInfo")
        .build()

    // realm
    private val bookRealm = Realm.getInstance(bookConfig)
    private val graphRealm = Realm.getInstance(graphConfig)
    private val userRealm = Realm.getInstance(userConfig)

    /**
     * bookRealmを返すメソッド
     */
    fun getBookRealmInstance(): Realm{
        return bookRealm
    }

    /**
     * graphRealmを返すメソッド
     */
    fun getGraphRealmInstance(): Realm {
        return graphRealm
    }

    fun getUserRealmInstance(): Realm {
        return userRealm
    }

    /**
     * realmを閉じるメソッド
     */
    fun close() {
        bookRealm.close()
        graphRealm.close()
        userRealm.close()
    }

    fun isClosed() :Boolean{
        return bookRealm.isClosed
    }



}