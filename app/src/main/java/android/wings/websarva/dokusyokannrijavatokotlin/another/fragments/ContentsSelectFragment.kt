package android.wings.websarva.dokusyokannrijavatokotlin.another.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.wings.websarva.dokusyokannrijavatokotlin.R
import android.wings.websarva.dokusyokannrijavatokotlin.another.AnotherUserTabAdapter
import kotlinx.android.synthetic.main.fragment_contents_select.*


class ContentsSelectFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_contents_select, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        userContentsViewPager.adapter = AnotherUserTabAdapter(childFragmentManager)
        userContentsTabLayout.setupWithViewPager(userContentsViewPager)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            ContentsSelectFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}