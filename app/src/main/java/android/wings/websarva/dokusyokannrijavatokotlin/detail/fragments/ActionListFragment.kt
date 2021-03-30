package android.wings.websarva.dokusyokannrijavatokotlin.detail.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.wings.websarva.dokusyokannrijavatokotlin.detail.ActionListAdapter
import android.wings.websarva.dokusyokannrijavatokotlin.R
import android.wings.websarva.dokusyokannrijavatokotlin.databinding.FragmentActionListBinding
import android.wings.websarva.dokusyokannrijavatokotlin.detail.fragments.base.BaseDetailFragment
import android.wings.websarva.dokusyokannrijavatokotlin.utils.DividerHelper
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import io.realm.Sort


class ActionListFragment : BaseDetailFragment() {

    private var _binding: FragmentActionListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentActionListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // アクションリストを取得
        bookObj?.actionPlanDairy?.sort("date", Sort.DESCENDING)?.let {

            //アダプターをセットする。
            val adapter = ActionListAdapter(it, true)
            adapter.setItemClickListener(object : ActionListAdapter.OnItemClickListener {
                override fun onItemClickListener(actionId: String) {
                    moveToNextFragment(ActionDetailFragment.newInstance(actionId))
                }
            })

            //リサイクラービューの設定
            binding.actionDetailRecyclerView.adapter = adapter
            binding.actionDetailRecyclerView.setHasFixedSize(true)
            binding.actionDetailRecyclerView.layoutManager = LinearLayoutManager(view.context)
            binding.actionDetailRecyclerView.addItemDecoration(DividerHelper.createDivider(requireContext()))
        }

        binding.addActionButton.setOnClickListener {
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
