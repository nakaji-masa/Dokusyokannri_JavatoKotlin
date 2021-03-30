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
    private var bookRealm = Realm.getInstance(bookConfig)
    private var graphRealm = Realm.getInstance(graphConfig)
    private var userRealm = Realm.getInstance(userConfig)

    /**
     * bookRealmを返すメソッド
     */
    fun getBookRealmInstance(): Realm {
        if (bookRealm.isClosed) {
            bookRealm = Realm.getInstance(bookConfig)
        }
        return bookRealm
    }

    /**
     * graphRealmを返すメソッド
     */
    fun getGraphRealmInstance(): Realm {
        if (graphRealm.isClosed) {
            graphRealm = Realm.getInstance(graphConfig)
        }
        return graphRealm
    }

    /**
     * userRealmを返すメソッド
     */
    fun getUserRealmInstance(): Realm {
        if (userRealm.isClosed) {
            userRealm = Realm.getInstance(userConfig)
        }
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
}