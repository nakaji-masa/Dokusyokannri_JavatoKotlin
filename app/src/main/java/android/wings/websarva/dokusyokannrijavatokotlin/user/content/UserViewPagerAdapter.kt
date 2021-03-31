package android.wings.websarva.dokusyokannrijavatokotlin.user.content

import android.wings.websarva.dokusyokannrijavatokotlin.user.content.another.fragments.AnotherBookshelfFragment
import android.wings.websarva.dokusyokannrijavatokotlin.user.content.another.fragments.AnotherPostFragment
import android.wings.websarva.dokusyokannrijavatokotlin.user.content.myshelf.fragments.MyBookShelfFragment
import android.wings.websarva.dokusyokannrijavatokotlin.user.content.myshelf.fragments.MyPostFragment
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class UserViewPagerAdapter(
    fragment: Fragment, private val anotherFlag: Boolean
) : FragmentStateAdapter(fragment) {

    override fun getItemCount() = 2


    override fun createFragment(position: Int): Fragment {
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