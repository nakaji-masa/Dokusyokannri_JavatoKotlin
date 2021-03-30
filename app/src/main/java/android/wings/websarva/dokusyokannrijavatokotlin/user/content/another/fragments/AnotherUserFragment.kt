package android.wings.websarva.dokusyokannrijavatokotlin.user.content.another.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.wings.websarva.dokusyokannrijavatokotlin.R
import android.wings.websarva.dokusyokannrijavatokotlin.databinding.FragmentUserBinding
import android.wings.websarva.dokusyokannrijavatokotlin.user.content.fragments.SelectContentFragment
import android.wings.websarva.dokusyokannrijavatokotlin.user.content.another.fragments.base.BaseAnotherUserFragment
import android.wings.websarva.dokusyokannrijavatokotlin.utils.GlideHelper


class AnotherUserFragment : BaseAnotherUserFragment() {
    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.myPageBar.visibility = View.GONE
        GlideHelper.viewUserImage(user.userImageUrl, binding.userPageImage)
        binding.userPageName.text = user.userName
        binding.userPageIntroduction.text = user.introduction

        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.add(
            R.id.userContentContainer,
            SelectContentFragment.newInstance(true)
        )
        transaction.commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            AnotherUserFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}