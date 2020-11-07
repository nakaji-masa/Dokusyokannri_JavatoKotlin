package android.wings.websarva.dokusyokannrijavatokotlin

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class BookListObject : RealmObject() {
    @PrimaryKey
    var id : Int = 0

    var title : String = ""

    var date : String = ""

    var notice : String = ""

    var actionPlan : String = ""

    var image : ByteArray? = null

    var actionPlanDairy : RealmList<BookActionPlanObject> = RealmList()
}