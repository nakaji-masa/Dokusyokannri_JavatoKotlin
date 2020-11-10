package android.wings.websarva.dokusyokannrijavatokotlin

import io.realm.RealmList
import io.realm.RealmObject

open class GraphObject : RealmObject(){
    var graphList : RealmList<GraphYearObject> = RealmList()
}