package android.wings.websarva.dokusyokannrijavatokotlin.realm.`object`

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

open class ActionPlanObject : RealmObject() {
    @PrimaryKey
    var id: String = UUID.randomUUID().toString()

    var date: Date = Date()

    var title: String = ""

    var what: String = ""

    var could: String = ""

    var couldNot: String = ""

    var nextAction: String = ""
}