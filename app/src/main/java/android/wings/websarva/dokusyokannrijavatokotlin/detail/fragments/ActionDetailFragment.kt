package android.wings.websarva.dokusyokannrijavatokotlin.detail.fragments

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import android.wings.websarva.dokusyokannrijavatokotlin.R
import android.wings.websarva.dokusyokannrijavatokotlin.activities.DetailActivity
import android.wings.websarva.dokusyokannrijavatokotlin.realm.`object`.ActionPlanObject
import android.wings.websarva.dokusyokannrijavatokotlin.realm.`object`.BookObject
import android.wings.websarva.dokusyokannrijavatokotlin.realm.config.RealmConfigObject
import io.realm.Realm
import io.realm.Sort
import kotlinx.android.synthetic.main.fragment_action_detail.*
import kotlinx.android.synthetic.main.fragment_action_detail.view.*
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

        // 最初のアクションプランは編集・削除不可にする
        if (obj?.title == getString(R.string.action_first)) {
            actionUpdateButton.visibility = View.INVISIBLE
            actionDeleteButton.visibility = View.INVISIBLE
        } else {
            view.actionUpdateButton.setOnClickListener {
                moveToInputActionPlanFragment()
            }

            view.actionDeleteButton.setOnClickListener {
                AlertDialog.Builder(activity, R.style.BasicDialogTheme)
                    .setMessage(R.string.dialog_delete_message)
                    .setPositiveButton(R.string.dialog_positive) { dialog, which ->
                        deleteData(obj)
                        dialog.dismiss()
                        activity?.supportFragmentManager?.popBackStack()
                    }
                    .setNegativeButton(R.string.dialog_negative) { dialog, which ->
                        dialog.dismiss()
                    }
                    .show()
            }
        }
    }

    /**
     * アクションプランの入力画面に遷移するメソッド
     */
    private fun moveToInputActionPlanFragment() {
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.replace(R.id.detailContainer, InputActionDairyDetailFragment.newInstance(bookId, actionId))
        transaction?.addToBackStack(null)
        transaction?.commit()
    }

    /**
     * アクションプランを削除するメソッド
     */
    private fun deleteData(obj: ActionPlanObject?) {
        realm.executeTransaction {
            obj?.deleteFromRealm()
            val newestObj = it.where(ActionPlanObject::class.java).findAll().sort("date", Sort.DESCENDING).first()
            val bookObject = it.where(BookObject::class.java).equalTo("id", bookId).findFirst()
            newestObj?.nextAction?.let { text ->
                bookObject?.actionPlan = text
            }
        }
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

