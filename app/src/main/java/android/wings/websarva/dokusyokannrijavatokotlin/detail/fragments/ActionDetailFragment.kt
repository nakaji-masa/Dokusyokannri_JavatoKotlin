package android.wings.websarva.dokusyokannrijavatokotlin.detail.fragments

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import android.wings.websarva.dokusyokannrijavatokotlin.R
import android.wings.websarva.dokusyokannrijavatokotlin.activities.DetailActivity
import android.wings.websarva.dokusyokannrijavatokotlin.realm.`object`.BookObject
import android.wings.websarva.dokusyokannrijavatokotlin.realm.config.RealmConfigObject
import io.realm.Realm
import java.text.SimpleDateFormat
import java.util.*


class ActionDetailFragment : Fragment() {

    private lateinit var realm: Realm
    private lateinit var bookId: String
    private lateinit var actionId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            bookId = it.getString(DetailActivity.BOOK_ID, "")
            actionId = it.getString(DetailActivity.ACTION_PLAN_ID, "")
        }
        realm = Realm.getInstance(RealmConfigObject.bookListConfig)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_action_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val title: TextView = view.findViewById(R.id.actionDetailTitle)
        val date: TextView = view.findViewById(R.id.actionDetailDate)
        val what: TextView = view.findViewById(R.id.actionDetailWhat)
        val could: TextView = view.findViewById(R.id.actionDetailCould)
        val couldNot: TextView = view.findViewById(R.id.actionDetailCouldNot)
        val next: TextView = view.findViewById(R.id.actionDetailNext)

        val book = realm.where(BookObject::class.java).equalTo("id", bookId).findFirst()
        val obj = book?.actionPlanDairy?.last { it.id == actionId }
        title.text = obj?.title
        date.text = convertDateToString(obj?.date)
        what.text = obj?.what
        could.text = obj?.could
        couldNot.text = obj?.couldNot
        next.text = obj?.nextAction
    }

    /**
     * Date型を"yyyy年MM月dd日の書式で返すメソッドです"
     * @param date 日付
     * @return String "yyyy年MM月dd"の文字列
     */
    private fun convertDateToString(date: Date?) : String {
        return if (date == null) {
            ""
        } else {
            val sdf = SimpleDateFormat("yyyy年MM月dd日", Locale.JAPAN)
            sdf.format(date)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    companion object {
        @JvmStatic
        fun newInstance(bookId: String, actionId: String) =
            ActionDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(DetailActivity.BOOK_ID, bookId)
                    putString(DetailActivity.ACTION_PLAN_ID, actionId)
                }
            }

    }


}

