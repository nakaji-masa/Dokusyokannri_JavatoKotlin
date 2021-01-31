package android.wings.websarva.dokusyokannrijavatokotlin.detail

import android.content.Context
import android.wings.websarva.dokusyokannrijavatokotlin.detail.fragments.ActionDairyFragment
import android.wings.websarva.dokusyokannrijavatokotlin.detail.fragments.DetailFragment
import android.wings.websarva.dokusyokannrijavatokotlin.detail.fragments.DetailPostFragment
import android.wings.websarva.dokusyokannrijavatokotlin.detail.fragments.OnActionDairyFragmentListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class DetailTabAdapter(fm : FragmentManager, val context : Context, val id : String?) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val titleList = listOf("本の詳細", "アクションプラン", "本の投稿")
    var onDetailPagerAdapterListener: OnDetailPagerAdapterListener? = null

    override fun getCount(): Int {
        //タブの数を返す
        return titleList.size
    }

    override fun getItem(position: Int): Fragment {
       //押しているタブによって返すフラグメントを変える
        return when(position) {
            0 -> {
                DetailFragment.newInstance(id)
            }

            1 -> {
                val fragment = ActionDairyFragment.newInstance(id)
                fragment.onActionDairyFragmentListener =
                    object : OnActionDairyFragmentListener {
                        override fun onAddButtonClicked(id: String) {
                           onDetailPagerAdapterListener?.onAddButtonClicked(id)
                        }

                        override fun onItemClicked(bookId: String, actionId: String) {
                            onDetailPagerAdapterListener?.onActionItemClicked(bookId, actionId)
                        }
                    }
                fragment
            }
            else ->  {
                DetailPostFragment.newInstance(id)
            }
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        //タブの文字を決める
        return when(position) {
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

interface OnDetailPagerAdapterListener {
    fun onActionItemClicked(bookId: String, actionId: String)
    fun onAddButtonClicked(bookId: String)
}