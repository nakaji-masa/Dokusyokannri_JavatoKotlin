
package android.wings.websarva.dokusyokannrijavatokotlin.realm.`object`

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class GraphObject : RealmObject(){
    @PrimaryKey
    var year = 0
    var graphList : RealmList<GraphMonthObject> = RealmList()
}