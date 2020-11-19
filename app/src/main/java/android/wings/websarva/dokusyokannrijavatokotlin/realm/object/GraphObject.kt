package android.wings.websarva.dokusyokannrijavatokotlin.realm.`object`

import io.realm.RealmList
import io.realm.RealmObject

open class GraphObject : RealmObject(){
    var graphList : RealmList<GraphYearObject> = RealmList()
}