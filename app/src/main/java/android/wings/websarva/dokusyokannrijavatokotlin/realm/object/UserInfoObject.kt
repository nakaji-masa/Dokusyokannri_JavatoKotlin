package android.wings.websarva.dokusyokannrijavatokotlin.realm.`object`

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

open class UserInfoObject: RealmObject() {
    @PrimaryKey
    var uid: String = ""

    var userName: String = ""

    var introduction: String = ""

    var imageUrl: String = ""
}