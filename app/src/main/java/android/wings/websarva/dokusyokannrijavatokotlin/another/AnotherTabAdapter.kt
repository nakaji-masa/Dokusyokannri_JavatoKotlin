package android.wings.websarva.dokusyokannrijavatokotlin.another

import android.wings.websarva.dokusyokannrijavatokotlin.another.fragments.BookshelfContentFragment
import android.wings.websarva.dokusyokannrijavatokotlin.another.fragments.PostContentFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class AnotherTabAdapter(
    fm: FragmentManager,
    private val uid: String,
    private val userJson: String
) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val titleList = listOf("本棚", "投稿")

    override fun getCount(): Int {
        return titleList.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
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
                BookshelfContentFragment.newInstance(uid)
            }

            else -> {
                PostContentFragment.newInstance(uid, userJson)
            }
        }
    }
}