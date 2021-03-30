package android.wings.websarva.dokusyokannrijavatokotlin.user.content

import android.wings.websarva.dokusyokannrijavatokotlin.user.content.another.fragments.AnotherBookshelfFragment
import android.wings.websarva.dokusyokannrijavatokotlin.user.content.another.fragments.AnotherPostFragment
import android.wings.websarva.dokusyokannrijavatokotlin.user.content.myshelf.fragments.MyBookShelfFragment
import android.wings.websarva.dokusyokannrijavatokotlin.user.content.myshelf.fragments.MyPostFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class UserTabAdapter(
    fm: FragmentManager, private val anotherFlag: Boolean
) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val titleList = listOf("本棚", "投稿")

    override fun getCount(): Int {
        return titleList.size
    }

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            0 -> {
                titleList[position]
            }

            else -> {
                titleList[position]
            }
        }
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                if (anotherFlag) AnotherBookshelfFragment.newInstance() else MyBookShelfFragment.newInstance()
            }

            else -> {
                if (anotherFlag) AnotherPostFragment.newInstance() else MyPostFragment.newInstance()
            }
        }
    }
}