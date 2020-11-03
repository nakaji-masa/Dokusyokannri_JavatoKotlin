package android.wings.websarva.dokusyokannrijavatokotlin

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class BookActionPlanObject : RealmObject() {
    @PrimaryKey
    var id : Int? = null

    var actionPlans : RealmList<String> = RealmList()
}