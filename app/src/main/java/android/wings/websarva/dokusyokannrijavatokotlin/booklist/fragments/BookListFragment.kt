package android.wings.websarva.dokusyokannrijavatokotlin.booklist.fragments

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.DisplayMetrics
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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

        // 画面のサイズを取得する
        val dm = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(dm)
        val width = dm.widthPixels / requireContext().resources.displayMetrics.density.toInt()

        // リサイクラービューの設定
        bookListRecyclerView.layoutManager = GridLayoutManager(
                view.context,
                3,
                GridLayoutManager.VERTICAL,
                false
            )
        bookListRecyclerView.addItemDecoration(GridItemDecoration(width, 3))
        bookListRecyclerView.adapter = adapter
        bookListRecyclerView.setHasFixedSize(true)

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

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            BookListFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

}

class GridItemDecoration(private val width: Int, private val spanCount: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val space = (width - 300) / 6
        outRect.left = space
        outRect.right = space
        outRect.bottom = space

        if (parent.getChildLayoutPosition(view) < spanCount)
            outRect.top = space

    }
}