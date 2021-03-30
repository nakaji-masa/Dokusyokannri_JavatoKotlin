package android.wings.websarva.dokusyokannrijavatokotlin.chart.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.wings.websarva.dokusyokannrijavatokotlin.chart.ChartTabAdapter
import android.wings.websarva.dokusyokannrijavatokotlin.databinding.FragmentChartSelectBinding
import androidx.fragment.app.Fragment


class ChartSelectFragment : Fragment() {

    private var _binding: FragmentChartSelectBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChartSelectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.graphViewPager.adapter = ChartTabAdapter(childFragmentManager)
        binding.graphTabLayout.setupWithViewPager(binding.graphViewPager)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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