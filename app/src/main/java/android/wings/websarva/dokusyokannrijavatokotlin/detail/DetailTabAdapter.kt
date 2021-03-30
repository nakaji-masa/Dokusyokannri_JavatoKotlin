package android.wings.websarva.dokusyokannrijavatokotlin.detail

import android.wings.websarva.dokusyokannrijavatokotlin.detail.fragments.ActionListFragment
import android.wings.websarva.dokusyokannrijavatokotlin.detail.fragments.DetailFragment
import android.wings.websarva.dokusyokannrijavatokotlin.detail.fragments.DetailPostFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class DetailTabAdapter(fm: FragmentManager) :
    FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val titleList = listOf("詳細", "投稿", "行動")

    override fun getCount(): Int {
        //タブの数を返す
        return titleList.size
    }

    override fun getItem(position: Int): Fragment {
        //押しているタブによって返すフラグメントを変える
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

    override fun getPageTitle(position: Int): CharSequence {
        //タブの文字を決める
        return when (position) {
            0 -> {
                titleList[position]
            }

            1 -> {
                titleList[position]
            }

            else -> {
                titleList[position]
            }
        }
    }
}
