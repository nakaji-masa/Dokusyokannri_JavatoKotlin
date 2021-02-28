package android.wings.websarva.dokusyokannrijavatokotlin.detail.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.wings.websarva.dokusyokannrijavatokotlin.R
import android.wings.websarva.dokusyokannrijavatokotlin.detail.activities.DetailActivity
import android.wings.websarva.dokusyokannrijavatokotlin.detail.DetailTabAdapter
import android.wings.websarva.dokusyokannrijavatokotlin.detail.fragments.base.BaseDetailFragment
import kotlinx.android.synthetic.main.fragment_detail_select.*


class DetailSelectFragment : BaseDetailFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_detail_select, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val adapter = DetailTabAdapter(childFragmentManager)

        //アダプターのセット
        detailPager.adapter = adapter

        //タブにpagerの情報をセット
        detailTabLayout.setupWithViewPager(detailPager)

        // 通知から画面遷移したときの場合
        if (requireActivity().intent.getBooleanExtra(TAB_POST_START, false)) {
            detailPager.currentItem = 2
        }
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