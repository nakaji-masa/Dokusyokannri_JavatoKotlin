package android.wings.websarva.dokusyokannrijavatokotlin

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class BookActionPlanObject : RealmObject() {
    var nextActionPlans: String = ""
    var actionPlans : String = ""
    var date : String = ""
}