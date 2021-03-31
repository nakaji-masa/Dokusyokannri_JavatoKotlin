package android.wings.websarva.dokusyokannrijavatokotlin.detail.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.wings.websarva.dokusyokannrijavatokotlin.databinding.FragmentDetailSelectBinding
import android.wings.websarva.dokusyokannrijavatokotlin.detail.activities.DetailActivity
import android.wings.websarva.dokusyokannrijavatokotlin.detail.DetailViewPagerAdapter
import android.wings.websarva.dokusyokannrijavatokotlin.detail.fragments.base.BaseDetailFragment
import com.google.android.material.tabs.TabLayoutMediator


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

        val adapter = DetailViewPagerAdapter(this)

        //アダプターのセット
        binding.detailPager.adapter = adapter

        TabLayoutMediator(binding.detailTabLayout, binding.detailPager) { tab, position ->
            tab.text = when (position) {
                0 -> { "詳細" }
                1 -> { "投稿" }
                else -> { "行動" }
            }
        }.attach()

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