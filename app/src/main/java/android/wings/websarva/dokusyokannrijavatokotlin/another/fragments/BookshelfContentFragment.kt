package android.wings.websarva.dokusyokannrijavatokotlin.another.fragments

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.wings.websarva.dokusyokannrijavatokotlin.R
import android.wings.websarva.dokusyokannrijavatokotlin.another.ContentBookShelfAdapter
import android.wings.websarva.dokusyokannrijavatokotlin.another.fragments.base.BaseAnotherUserFragment
import android.wings.websarva.dokusyokannrijavatokotlin.bookshelf.fragments.GridItemDecoration
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.FireStoreHelper
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.fragment_bookshelf_content.*


class BookshelfContentFragment : BaseAnotherUserFragment() {

    private lateinit var adapter: ContentBookShelfAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bookshelf_content, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val dm = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(dm)
        val width = dm.widthPixels / requireContext().resources.displayMetrics.density.toInt()

        contentBookShelfRecyclerView.layoutManager = GridLayoutManager(
            requireContext(),
            3,
            GridLayoutManager.VERTICAL,
            false
        )

        contentBookShelfRecyclerView.addItemDecoration(GridItemDecoration(width, 3))
        contentBookShelfRecyclerView.setHasFixedSize(true)
        adapter = ContentBookShelfAdapter(FireStoreHelper.getRecyclerOptionsFromUid())
        contentBookShelfRecyclerView.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            BookshelfContentFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}