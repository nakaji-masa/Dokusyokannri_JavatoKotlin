package android.wings.websarva.dokusyokannrijavatokotlin.realm.`object`


import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

open class BookObject : RealmObject() {
    @PrimaryKey
    var id : String = ""

    var title : String = ""

    var date : String = ""

    var actionPlan : String = ""

    var imageUrl : String = ""

    var author : String = ""

    var uid : String = ""

    var createdAt : Date = Date()

    var actionPlanDairy : RealmList<ActionPlanObject> = RealmList()

}