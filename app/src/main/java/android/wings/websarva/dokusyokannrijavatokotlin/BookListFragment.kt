package android.wings.websarva.dokusyokannrijavatokotlin

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import io.realm.Realm
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.fragment_book_list.*


class BookListFragment : Fragment() {

    private lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        realm = Realm.getDefaultInstance()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_book_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val bookList = realm.where(BookListObject::class.java).findAll()
        val adapter = BookListAdapterMain(view.context, bookList, true)

        book_recyclerview.setHasFixedSize(true)
        book_recyclerview.layoutManager = LinearLayoutManager(view.context)
        book_recyclerview.adapter = adapter
        val itemDecoration = DividerItemDecoration(view.context, DividerItemDecoration.VERTICAL)
        book_recyclerview.addItemDecoration(itemDecoration)

        adapter.setOnItemClickListener(object : BookListAdapterMain.OnItemClickListener{
            override fun onItemClickListener(view: View, position: Int, clickedId: Int?) {
                val intent = Intent(view.context, Detail::class.java)
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