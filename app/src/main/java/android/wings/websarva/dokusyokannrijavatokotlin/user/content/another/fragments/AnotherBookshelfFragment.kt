package android.wings.websarva.dokusyokannrijavatokotlin.user.content.another.fragments

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.wings.websarva.dokusyokannrijavatokotlin.databinding.FragmentAnotherBookshelfBinding
import android.wings.websarva.dokusyokannrijavatokotlin.user.content.another.AnotherUserBookShelfAdapter
import android.wings.websarva.dokusyokannrijavatokotlin.user.content.another.fragments.base.BaseAnotherUserFragment
import android.wings.websarva.dokusyokannrijavatokotlin.user.content.myshelf.fragments.GridItemDecoration
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.FireStoreHelper
import androidx.recyclerview.widget.GridLayoutManager


class AnotherBookshelfFragment : BaseAnotherUserFragment() {

    private lateinit var adapterAnotherUser: AnotherUserBookShelfAdapter
    private var _binding: FragmentAnotherBookshelfBinding? = null
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
        _binding = FragmentAnotherBookshelfBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val dm = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(dm)
        val width = dm.widthPixels / requireContext().resources.displayMetrics.density.toInt()

        binding.anotherBookShelfRecyclerView.layoutManager = GridLayoutManager(
            requireContext(),
            3,
            GridLayoutManager.VERTICAL,
            false
        )

        binding.anotherBookShelfRecyclerView.addItemDecoration(GridItemDecoration(width, 3))
        binding.anotherBookShelfRecyclerView.setHasFixedSize(true)

        adapterAnotherUser = AnotherUserBookShelfAdapter(FireStoreHelper.getRecyclerOptionsFromUid(user.uid))
        binding.anotherBookShelfRecyclerView.adapter = adapterAnotherUser
    }

    override fun onStart() {
        super.onStart()
        adapterAnotherUser.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapterAnotherUser.stopListening()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            AnotherBookshelfFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}