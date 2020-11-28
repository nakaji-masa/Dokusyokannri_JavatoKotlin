package android.wings.websarva.dokusyokannrijavatokotlin.booklist.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.wings.websarva.dokusyokannrijavatokotlin.R
import android.wings.websarva.dokusyokannrijavatokotlin.activities.DetailActivity
import android.wings.websarva.dokusyokannrijavatokotlin.activities.RegisterActivity
import android.wings.websarva.dokusyokannrijavatokotlin.booklist.BookListAdapter
import android.wings.websarva.dokusyokannrijavatokotlin.realm.`object`.BookListObject
import android.wings.websarva.dokusyokannrijavatokotlin.realm.config.RealmConfigObject
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_book_list.*

class BookListFragment : Fragment() {

    private lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        realm = Realm.getInstance(RealmConfigObject.bookListConfig)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_book_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val bookList = realm.where(BookListObject::class.java).findAll()
        val adapter = BookListAdapter(view.context, bookList, true)

        bookListRecyclerView.setHasFixedSize(true)
        bookListRecyclerView.layoutManager = LinearLayoutManager(view.context)
        bookListRecyclerView.adapter = adapter
        val itemDecoration = DividerItemDecoration(view.context, DividerItemDecoration.VERTICAL)
        bookListRecyclerView.addItemDecoration(itemDecoration)

        adapter.setOnItemClickListener(object : BookListAdapter.OnItemClickListener {
            override fun onItemClickListener(view: View, position: Int, clickedId: Int?) {
                val intent = Intent(view.context, DetailActivity::class.java)
                intent.putExtra("id", clickedId)
                startActivity(intent)
            }
        })

        add_fab.setOnClickListener {
            val intent = Intent(view.context, RegisterActivity::class.java)
            startActivity(intent)
        }

    }


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BookListFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
}