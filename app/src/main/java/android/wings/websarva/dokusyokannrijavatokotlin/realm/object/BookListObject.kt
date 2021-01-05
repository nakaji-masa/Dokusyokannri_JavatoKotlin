package android.wings.websarva.dokusyokannrijavatokotlin.realm.`object`

import android.wings.websarva.dokusyokannrijavatokotlin.realm.`object`.ActionPlanObject
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class BookListObject : RealmObject() {
    @PrimaryKey
    var id : Int = 0

    var title : String = ""

    var date : String = ""

    var actionPlan : String = ""

    var image : String = ""

    var actionPlanDairy : RealmList<ActionPlanObject> = RealmList()

}