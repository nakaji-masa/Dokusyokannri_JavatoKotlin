package android.wings.websarva.dokusyokannrijavatokotlin.utils

import java.text.SimpleDateFormat
import java.util.*
import java.util.Date

object GetDateObject {
    fun getToday(): String {
        val sdf = SimpleDateFormat("yyyy年MM月dd日", Locale.JAPAN)
        val date = Date()
        return sdf.format(date)
    }
}