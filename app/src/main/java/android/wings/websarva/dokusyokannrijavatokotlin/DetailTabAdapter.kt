package android.wings.websarva.dokusyokannrijavatokotlin

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

@Suppress("DEPRECATION")
class DetailTabAdapter(fm : FragmentManager, val context : Context, val id : Int) : FragmentPagerAdapter(fm) {

    override fun getCount(): Int {
        //タブの数を返す
        return 2
    }

    override fun getItem(position: Int): Fragment {
       //押しているタブによって返すフラグメントを変える
        when(position) {
            0 -> {
                return DetailFragment.newInstance(id)
            }

            else -> {
                return DetailFragment2()
            }
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        //タブの文字を決める
        when(position) {
            0 -> {
                return "本の詳細"
            }

            else -> {
                return "アクションプランの経過"
            }
        }
    }
}