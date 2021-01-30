package android.wings.websarva.dokusyokannrijavatokotlin.main

import android.wings.websarva.dokusyokannrijavatokotlin.bookshelf.fragments.BookShelfFragment
import android.wings.websarva.dokusyokannrijavatokotlin.graph.fragments.GraphFragment
import android.wings.websarva.dokusyokannrijavatokotlin.library.fragments.LibraryFragment
import android.wings.websarva.dokusyokannrijavatokotlin.settings.fragments.SettingsFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class MainFragmentPagerAdapter (fm: FragmentManager): FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getCount(): Int {
        return 4
    }

    override fun getItem(position: Int): Fragment {
        return when(position) {
            0 -> {
                BookShelfFragment.newInstance()
            }

            1 -> {
                GraphFragment.newInstance()
            }

            2 -> {
                LibraryFragment.newInstance()
            }

            else  -> {
                SettingsFragment.newInstance()
            }
        }
    }

}
