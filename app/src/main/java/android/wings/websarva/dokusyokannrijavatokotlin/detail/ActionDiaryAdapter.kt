package android.wings.websarva.dokusyokannrijavatokotlin.detail

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.wings.websarva.dokusyokannrijavatokotlin.R
import android.wings.websarva.dokusyokannrijavatokotlin.realm.`object`.ActionPlanObject
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import io.realm.RealmResults
import java.text.SimpleDateFormat
import java.util.*

class ActionDiaryAdapter(
    private val actionPlanList: RealmResults<ActionPlanObject>?
) : RecyclerView.Adapter<ActionDiaryAdapter.ViewHolder>() {

    lateinit var listener: OnItemClickListener

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val calendar: ImageView = view.findViewById(R.id.calendarImage)
        val calendarText: TextView = view.findViewById(R.id.calendarText)
        val title: TextView = view.findViewById(R.id.actionTitle)
        val date: TextView = view.findViewById(R.id.actionDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.action_diary_cell, parent, false)
        return ViewHolder(view)
    }


    override fun getItemCount(): Int {
        return actionPlanList?.size!!
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val actionPlan = actionPlanList?.get(position)

        // colorId
        val colorId = getCalenderColor(getMonth(actionPlan?.date!!))

        // context
        val context = holder.itemView.context

        // drawable
        val drawable = ContextCompat.getDrawable(context, R.drawable.ic_calendar)?.mutate()
        drawable?.colorFilter = PorterDuffColorFilter(ContextCompat.getColor(context, colorId), PorterDuff.Mode.SRC_IN)

        // UI実装
        holder.calendar.setImageDrawable(drawable)
        holder.calendarText.text = getDay(actionPlan.date)
        holder.calendarText.setTextColor(ContextCompat.getColor(holder.itemView.context,colorId))
        holder.title.text = actionPlan.title
        holder.date.text = getDateString(actionPlan.date)

        // リスナーの実装
        holder.itemView.setOnClickListener {
            listener.onItemClickListener(it, position, actionPlan.id)
        }
    }


    /**
     * 登録した月を引数として受け取り、色のidを戻り値として返す。
     * @param month　月
     * @return Int　color_id
     */
    private fun getCalenderColor(month: String): Int {
        when(month) {
            "1" -> {
                return R.color.colorJanuary
            }

            "2" -> {
                return R.color.colorFebruary
            }

            "3" -> {
                return R.color.colorMarch
            }

            "4" -> {
                return R.color.colorApril
            }

            "5" -> {
                return R.color.colorMay
            }

            "6" -> {
                return R.color.colorJune
            }

            "7" -> {
                return R.color.colorJuly
            }

            "8" -> {
                return R.color.colorAugust
            }

            "9" -> {
                return R.color.colorSeptember
            }

            "10" -> {
                return R.color.colorOctober
            }

            "11" -> {
                return R.color.colorNovember
            }

            else -> {
                return R.color.colorDecember
            }
        }
    }

    /**
     * 引数のDate型を"yyyy年MM月dd日"の形式で返す
     * @param date 日付
     * @return String　"yyyy年MM月dd日"の形式の文字列
     */
    private fun getDateString(date: Date): String {
        val sdf = SimpleDateFormat("yyyy年MM月dd日", Locale.JAPAN)
        return sdf.format(date)
    }

    /**
     * 引数のDate型を"d日"の形式で返す
     * @param date 日付
     * @return String "d日"の形式の文字列
     */
    private fun getDay(date: Date): String {
        val sdf = SimpleDateFormat("d", Locale.JAPAN)
        return sdf.format(date)
    }

    /**
     * 引数のDate型を"M"の形式で返す
     * @param date 日付
     * @return　String "M"形式の文字列
     */
    private fun getMonth(date: Date): String {
        val sdf = SimpleDateFormat("M", Locale.JAPAN)
        return sdf.format(date)
    }

    interface OnItemClickListener {
        fun onItemClickListener(view: View, position: Int, clickedId: String)
    }

    /**
     * フィールドのlistenerをセットする
     * @param listener OnItemClickListener型のインタフェース
     */
    fun setItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }
}