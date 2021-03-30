package android.wings.websarva.dokusyokannrijavatokotlin.detail.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.wings.websarva.dokusyokannrijavatokotlin.databinding.FragmentDetailSelectBinding
import android.wings.websarva.dokusyokannrijavatokotlin.detail.activities.DetailActivity
import android.wings.websarva.dokusyokannrijavatokotlin.detail.DetailTabAdapter
import android.wings.websarva.dokusyokannrijavatokotlin.detail.fragments.base.BaseDetailFragment


class DetailSelectFragment : BaseDetailFragment() {

    private var _binding: FragmentDetailSelectBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailSelectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val adapter = DetailTabAdapter(childFragmentManager)

        //アダプターのセット
        binding.detailPager.adapter = adapter

        //タブにpagerの情報をセット
        binding.detailTabLayout.setupWithViewPager(binding.detailPager)

        // 通知から画面遷移したときの場合
        if (requireActivity().intent.getBooleanExtra(TAB_POST_START, false)) {
            binding.detailPager.currentItem = 1
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(id: String?) =
            DetailSelectFragment().apply {
                arguments = Bundle().apply {
                    putString(DetailActivity.BOOK_ID, id)
                }
            }

        const val TAB_POST_START = "tab_post_start"
    }
}