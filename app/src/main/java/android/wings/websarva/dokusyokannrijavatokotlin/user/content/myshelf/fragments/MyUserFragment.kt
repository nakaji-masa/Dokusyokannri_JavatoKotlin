package android.wings.websarva.dokusyokannrijavatokotlin.user.content.myshelf.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.wings.websarva.dokusyokannrijavatokotlin.R
import android.wings.websarva.dokusyokannrijavatokotlin.databinding.FragmentUserBinding
import android.wings.websarva.dokusyokannrijavatokotlin.user.content.fragments.SelectContentFragment
import android.wings.websarva.dokusyokannrijavatokotlin.user.content.myshelf.fragments.base.BaseMyFragment
import android.wings.websarva.dokusyokannrijavatokotlin.utils.GlideHelper

class MyUserFragment : BaseMyFragment() {

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
        user.let {
            GlideHelper.viewUserImage(it.userImageUrl, binding.userPageImage)
            binding.userPageName.text = it.userName
            binding.userPageIntroduction.text = it.introduction
        }

        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.add(R.id.userContentContainer, SelectContentFragment.newInstance(false))
        transaction.commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            MyUserFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}