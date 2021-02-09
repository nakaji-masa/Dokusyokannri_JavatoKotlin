package android.wings.websarva.dokusyokannrijavatokotlin.main.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.wings.websarva.dokusyokannrijavatokotlin.R
import android.wings.websarva.dokusyokannrijavatokotlin.library.fragments.LibraryFragment
import android.wings.websarva.dokusyokannrijavatokotlin.main.MainFragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.fragment_main_select.*


class MainSelectFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main_select, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        activity?.setTitle(R.string.title_bookshelf)
        mainViewPager.addOnPageChangeListener(object: ViewPager.OnPageChangeListener{
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                bottomNavigationView.menu.getItem(position).isChecked = true
                when (position) {
                    0 -> {
                        LibraryFragment.initializeActionBar(activity)
                        activity?.setTitle(R.string.title_bookshelf)
                    }

                    1 -> {
                        LibraryFragment.initializeActionBar(activity)
                        activity?.setTitle(R.string.title_graph)
                    }

                    2 -> {
                    }

                    else -> {
                        LibraryFragment.initializeActionBar(activity)
                        activity?.setTitle(R.string.title_settings)
                    }
                }
            }

            override fun onPageScrollStateChanged(state: Int) {

            }

        })
        mainViewPager.adapter = MainFragmentPagerAdapter(childFragmentManager)
        bottomNavigationView.selectedItemId = R.id.book_read_item
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.book_read_item -> {
                    mainViewPager.currentItem = 0
                    activity?.setTitle(R.string.title_bookshelf)
                    LibraryFragment.initializeActionBar(activity)
                }

                R.id.graph_item -> {
                    mainViewPager.currentItem = 1
                    activity?.setTitle(R.string.title_graph)
                    LibraryFragment.initializeActionBar(activity)
                }

                R.id.library_item -> {
                    mainViewPager.currentItem = 2
                }


                R.id.settings_item -> {
                    mainViewPager.currentItem = 3
                    activity?.setTitle(R.string.title_settings)
                    LibraryFragment.initializeActionBar(activity)
                }

            }
            return@setOnNavigationItemSelectedListener true
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            MainSelectFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}