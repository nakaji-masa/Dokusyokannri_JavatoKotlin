package android.wings.websarva.dokusyokannrijavatokotlin.detail.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.wings.websarva.dokusyokannrijavatokotlin.detail.ActionListAdapter
import android.wings.websarva.dokusyokannrijavatokotlin.R
import android.wings.websarva.dokusyokannrijavatokotlin.detail.fragments.base.BaseDetailFragment
import android.wings.websarva.dokusyokannrijavatokotlin.utils.DividerHelper
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_action_list.*


class ActionListFragment : BaseDetailFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_action_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // アクションリストを取得
        bookObj?.actionPlanDairy?.let {

            //アダプターをセットする。
            val adapter = ActionListAdapter(it, true)
            adapter.setItemClickListener(object : ActionListAdapter.OnItemClickListener {
                override fun onItemClickListener(actionId: String) {
                    moveToNextFragment(ActionDetailFragment.newInstance(actionId))
                }
            })

            //リサイクラービューの設定
            actionDetailRecyclerView.adapter = adapter
            actionDetailRecyclerView.setHasFixedSize(true)
            actionDetailRecyclerView.layoutManager = LinearLayoutManager(view.context)
            actionDetailRecyclerView.addItemDecoration(DividerHelper.createDivider(requireContext()))
        }

        addActionButton.setOnClickListener {
            moveToNextFragment(InputActionFragment.newInstance())
        }
    }

    /**
     * 次のフラグメントに遷移させるメソッド
     * @param fragment 遷移先のフラグメント
     */
    private fun moveToNextFragment(fragment: Fragment) {
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.addToBackStack(null)
        transaction.replace(R.id.detailContainer, fragment)
        transaction.commit()
    }

    companion object {

        const val ACTION_ID = "action_id"

        @JvmStatic
        fun newInstance() =
            ActionListFragment().apply {
                arguments = Bundle().apply {
                }
            }

    }

}
