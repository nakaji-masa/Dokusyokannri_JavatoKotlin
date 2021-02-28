package android.wings.websarva.dokusyokannrijavatokotlin.chart

import android.wings.websarva.dokusyokannrijavatokotlin.chart.fragments.BarChartFragment
import android.wings.websarva.dokusyokannrijavatokotlin.chart.fragments.PieChartFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class ChartTabAdapter(fm: FragmentManager): FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val titleList = listOf("読書量", "読書と行動の比率")

    override fun getCount(): Int {
        return titleList.size
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                BarChartFragment.newInstance()
            }

            else -> {
                PieChartFragment.newInstance()
            }
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> {
                titleList[0]
            }

            else -> {
                titleList[1]
            }
        }
    }

}