package android.wings.websarva.dokusyokannrijavatokotlin.chart.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.wings.websarva.dokusyokannrijavatokotlin.R
import android.wings.websarva.dokusyokannrijavatokotlin.chart.ChartTabAdapter
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_chart_select.*


class ChartSelectFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chart_select, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        graphViewPager.adapter = ChartTabAdapter(childFragmentManager)
        graphTabLayout.setupWithViewPager(graphViewPager)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            ChartSelectFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }


}