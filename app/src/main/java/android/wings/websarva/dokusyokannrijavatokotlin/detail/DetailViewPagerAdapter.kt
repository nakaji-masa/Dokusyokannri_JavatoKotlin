package android.wings.websarva.dokusyokannrijavatokotlin.detail

import android.wings.websarva.dokusyokannrijavatokotlin.detail.fragments.ActionListFragment
import android.wings.websarva.dokusyokannrijavatokotlin.detail.fragments.DetailFragment
import android.wings.websarva.dokusyokannrijavatokotlin.detail.fragments.DetailPostFragment
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class DetailViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount() = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                DetailFragment.newInstance()
            }
            1 -> {
                DetailPostFragment.newInstance()
            }
            else -> {
                ActionListFragment.newInstance()
            }
        }
    }
}
