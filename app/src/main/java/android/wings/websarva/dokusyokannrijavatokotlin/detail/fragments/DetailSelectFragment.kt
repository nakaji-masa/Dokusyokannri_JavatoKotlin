package android.wings.websarva.dokusyokannrijavatokotlin.detail.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.wings.websarva.dokusyokannrijavatokotlin.R
import android.wings.websarva.dokusyokannrijavatokotlin.detail.activities.DetailActivity
import android.wings.websarva.dokusyokannrijavatokotlin.detail.DetailTabAdapter
import android.wings.websarva.dokusyokannrijavatokotlin.detail.OnDetailPagerAdapterListener
import kotlinx.android.synthetic.main.fragment_detail_select.*


class DetailSelectFragment : Fragment() {

    private lateinit var bookId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            bookId = it.getString(DetailActivity.BOOK_ID, "")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_detail_select, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val adapter = DetailTabAdapter(childFragmentManager, requireContext(), bookId)

        //アダプターのセット
        detailPager.adapter = adapter

        //タブにpagerの情報をセット
        detailTabLayout.setupWithViewPager(detailPager)


        adapter.onDetailPagerAdapterListener =
            object : OnDetailPagerAdapterListener {
                override fun onActionItemClicked(bookId: String, actionId: String) {
                    val transaction = activity?.supportFragmentManager?.beginTransaction()
                    transaction?.addToBackStack(null)
                    transaction?.replace(
                        R.id.detailContainer,
                        ActionDetailFragment.newInstance(bookId, actionId)
                    )
                    transaction?.commit()
                }

                override fun onAddButtonClicked(bookId: String) {
                    val transaction = activity?.supportFragmentManager?.beginTransaction()
                    transaction?.addToBackStack(null)
                    transaction?.replace(
                        R.id.detailContainer,
                        InputActionDairyDetailFragment.newInstance(bookId)
                    )
                    transaction?.commit()
                }
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
    }
}