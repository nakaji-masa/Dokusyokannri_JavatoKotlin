package android.wings.websarva.dokusyokannrijavatokotlin.utils

import java.text.SimpleDateFormat
import java.util.*
import java.util.Date

object DateHelper {
    /**
     * 今日の日付を文字列形式で返すメソッド
     * @return String
     */
    fun getToday(): String {
        val sdf = SimpleDateFormat("yyyy年MM月dd日", Locale.JAPAN)
        val date = Date()
        return sdf.format(date)
    }

}