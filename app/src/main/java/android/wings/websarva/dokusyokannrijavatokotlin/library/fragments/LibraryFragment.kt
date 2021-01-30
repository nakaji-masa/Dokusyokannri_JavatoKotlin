package android.wings.websarva.dokusyokannrijavatokotlin.library.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.wings.websarva.dokusyokannrijavatokotlin.R
import android.wings.websarva.dokusyokannrijavatokotlin.library.LibraryAdapter
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.FireStoreHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class LibraryFragment : Fragment() {

    lateinit var adapter: LibraryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { 
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_library, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recyclerView: RecyclerView = view.findViewById(R.id.usersBookList)
        recyclerView.layoutManager = LinearLayoutManager(view.context)
        recyclerView.setHasFixedSize(true)
        adapter = LibraryAdapter(FireStoreHelper.getRecyclerOptions())
        recyclerView.adapter = adapter
        adapter.setItemClickListener(object: LibraryAdapter.OnCommentClickListener {
            override fun onCommentClickListener(userJson: String, bookJson: String) {
                val transaction = activity?.supportFragmentManager?.beginTransaction()
                transaction?.addToBackStack(null)
                transaction?.replace(R.id.mainContainer, PostDetailFragment.newInstance(userJson, bookJson))
                transaction?.commit()
            }
        })
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
            LibraryFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

}