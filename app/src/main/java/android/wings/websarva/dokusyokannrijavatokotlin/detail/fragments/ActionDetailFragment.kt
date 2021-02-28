package android.wings.websarva.dokusyokannrijavatokotlin.detail.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.wings.websarva.dokusyokannrijavatokotlin.R
import android.wings.websarva.dokusyokannrijavatokotlin.detail.fragments.base.BaseDetailFragment
import android.wings.websarva.dokusyokannrijavatokotlin.realm.`object`.ActionPlanObject
import android.wings.websarva.dokusyokannrijavatokotlin.realm.`object`.BookObject
import io.realm.Sort
import kotlinx.android.synthetic.main.fragment_action_detail.*
import kotlinx.android.synthetic.main.fragment_action_detail.view.*
import java.text.SimpleDateFormat
import java.util.*


class ActionDetailFragment : BaseDetailFragment() {

    private lateinit var actionId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            actionId = it.getString(ActionListFragment.ACTION_ID, "")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_action_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        
        // 行動の情報を取得し表示させる
        val actionObj = bookObj?.actionPlanDairy?.last { it.id == actionId }
        actionDetailTitle.text = actionObj?.title
        actionDetailDate.text = convertDateToString(actionObj?.date)
        actionDetailWhat.text = actionObj?.what
        actionDetailCould.text = actionObj?.could
        actionDetailCouldNot.text = actionObj?.couldNot
        actionDetailNext.text = actionObj?.nextAction

        // 最初のアクションプランは編集・削除不可にする
        if (actionObj?.title == getString(R.string.action_first)) {
            actionUpdateButton.visibility = View.INVISIBLE
            actionDeleteButton.visibility = View.INVISIBLE
        } else {
            view.actionUpdateButton.setOnClickListener {
                moveToInputActionPlanFragment()
            }

            actionDeleteButton.setOnClickListener {
                AlertDialog.Builder(requireContext())
                    .setMessage(R.string.dialog_delete_message)
                    .setPositiveButton(R.string.dialog_positive) { dialog, _ ->
                        deleteData(actionObj)
                        dialog.dismiss()
                        activity?.supportFragmentManager?.popBackStack()
                    }
                    .setNegativeButton(R.string.dialog_negative) { dialog, _ ->
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
        transaction?.replace(R.id.detailContainer, InputActionFragment.newInstance(actionId))
        transaction?.addToBackStack(null)
        transaction?.commit()
    }

    /**
     * アクションプランを削除するメソッド
     */
    private fun deleteData(obj: ActionPlanObject?) {
        realm.executeTransaction {
            obj?.deleteFromRealm()
            val newestObj =
                it.where(ActionPlanObject::class.java).findAll().sort("date", Sort.DESCENDING)
                    .first()
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
    private fun convertDateToString(date: Date?): String {
        return if (date == null) {
            ""
        } else {
            val sdf = SimpleDateFormat("yyyy年MM月dd日", Locale.JAPAN)
            sdf.format(date)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(id: String) =
            ActionDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(ActionListFragment.ACTION_ID, id)
                }
            }

    }
}

