package android.wings.websarva.dokusyokannrijavatokotlin.bookshelf.fragments

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.wings.websarva.dokusyokannrijavatokotlin.R
import android.wings.websarva.dokusyokannrijavatokotlin.detail.activities.DetailActivity
import android.wings.websarva.dokusyokannrijavatokotlin.activities.RegisterActivity
import android.wings.websarva.dokusyokannrijavatokotlin.bookshelf.BookshelfAdapter
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.AuthHelper
import android.wings.websarva.dokusyokannrijavatokotlin.realm.`object`.BookObject
import android.wings.websarva.dokusyokannrijavatokotlin.realm.config.RealmConfigObject
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_book_shelf.*


class BookShelfFragment : Fragment() {

    private lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        realm = Realm.getInstance(RealmConfigObject.bookConfig)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_book_shelf, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val bookList = realm.where(BookObject::class.java).equalTo("uid", AuthHelper.getUid()).findAll()
        val adapter = BookshelfAdapter(view.context, bookList, true)

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

        adapter.setOnItemClickListener(object : BookshelfAdapter.OnItemClickListener {
            override fun onItemClickListener(view: View, position: Int, clickedId: String?) {
                val intent = Intent(view.context, DetailActivity::class.java)
                intent.putExtra(DetailActivity.BOOK_ID, clickedId)
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
            BookShelfFragment().apply {
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