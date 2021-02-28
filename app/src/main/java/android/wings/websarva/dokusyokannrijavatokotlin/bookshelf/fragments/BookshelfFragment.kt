package android.wings.websarva.dokusyokannrijavatokotlin.bookshelf.fragments

import io.realm.RealmResults
import io.realm.Sort

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.wings.websarva.dokusyokannrijavatokotlin.R
import android.wings.websarva.dokusyokannrijavatokotlin.detail.activities.DetailActivity
import android.wings.websarva.dokusyokannrijavatokotlin.activities.RegisterActivity
import android.wings.websarva.dokusyokannrijavatokotlin.bookshelf.BookshelfAdapter
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.AuthHelper
import android.wings.websarva.dokusyokannrijavatokotlin.realm.`object`.BookObject
import android.wings.websarva.dokusyokannrijavatokotlin.realm.manager.RealmManager
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_book_shelf.*


class BookShelfFragment : Fragment() {

    private lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        realm = RealmManager.getBookRealmInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_book_shelf, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val bookList =
            realm.where(BookObject::class.java).equalTo("uid", AuthHelper.getUid()).findAll()
        val adapter = BookshelfAdapter(requireContext(), bookList, true)

        // 画面のサイズを取得する
        val dm = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(dm)
        val width = dm.widthPixels / requireContext().resources.displayMetrics.density.toInt()

        // リサイクラービューの設定
        bookListRecyclerView.layoutManager = GridLayoutManager(
            requireContext(),
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

        spinnerDate.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectText = parent?.selectedItem.toString()
                val stringArray = resources.getStringArray(R.array.spinner_text_array)
                if (selectText == stringArray[0]) {
                    displayOrderByAsc()
                } else {
                    displayOrderByDesc()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        add_fab.setOnClickListener {
            val intent = Intent(view.context, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * 本棚を表示するメソッドです。
     * @param bookList realmの取得結果
     */
    private fun displayBookShelf(bookList: RealmResults<BookObject>) {
        val adapter = BookshelfAdapter(requireContext(), bookList, true)
        bookListRecyclerView.adapter = adapter
        adapter.setOnItemClickListener(object : BookshelfAdapter.OnItemClickListener {
            override fun onItemClickListener(view: View, position: Int, clickedId: String?) {
                val intent = Intent(view.context, DetailActivity::class.java)
                intent.putExtra(DetailActivity.BOOK_ID, clickedId)
                startActivity(intent)
            }
        })
    }

    /**
     * 登録された順に本棚を表示するメソッド
     */
    private fun displayOrderByAsc() {
        val bookList =
            realm.where(BookObject::class.java).equalTo("uid", AuthHelper.getUid()).findAll()
                .sort("createdAt")
        displayBookShelf(bookList)
    }

    /**
     * 登録が新しい順に本棚を表示するメソッド
     */
    private fun displayOrderByDesc() {
        val bookList =
            realm.where(BookObject::class.java).equalTo("uid", AuthHelper.getUid()).findAll()
                .sort("createdAt", Sort.DESCENDING)
        displayBookShelf(bookList)
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

class GridItemDecoration(private val width: Int, private val spanCount: Int) :
    RecyclerView.ItemDecoration() {
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