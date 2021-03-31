package android.wings.websarva.dokusyokannrijavatokotlin.user.content.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.wings.websarva.dokusyokannrijavatokotlin.databinding.FragmentSelectContentBinding
import android.wings.websarva.dokusyokannrijavatokotlin.user.content.UserViewPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator


class SelectContentFragment : Fragment() {

    private var anotherFlag = false
    private var _binding: FragmentSelectContentBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            anotherFlag = it.getBoolean(ANOTHER_FLAG_KEY)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectContentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.userContentsViewPager.adapter = UserViewPagerAdapter(this, anotherFlag)

        TabLayoutMediator(binding.userContentsTabLayout, binding.userContentsViewPager) { tab, position ->
            tab.text = when (position) {
                0 -> { "本棚" }
                else -> { "投稿" }
            }
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        private const val ANOTHER_FLAG_KEY = "another_flag_key"

        @JvmStatic
        fun newInstance(flag: Boolean) =
            SelectContentFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(ANOTHER_FLAG_KEY, flag)
                }
            }
    }
}