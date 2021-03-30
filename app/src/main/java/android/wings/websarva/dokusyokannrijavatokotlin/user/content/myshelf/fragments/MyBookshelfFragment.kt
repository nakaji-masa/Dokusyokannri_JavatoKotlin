package android.wings.websarva.dokusyokannrijavatokotlin.user.content.myshelf.fragments

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
import android.wings.websarva.dokusyokannrijavatokotlin.databinding.FragmentMyBookshelfBinding
import android.wings.websarva.dokusyokannrijavatokotlin.user.content.myshelf.MyBookshelfAdapter
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.AuthHelper
import android.wings.websarva.dokusyokannrijavatokotlin.realm.`object`.BookObject
import android.wings.websarva.dokusyokannrijavatokotlin.realm.manager.RealmManager
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.realm.Realm


class MyBookShelfFragment : Fragment() {

    private lateinit var realm: Realm
    private var _binding: FragmentMyBookshelfBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        realm = RealmManager.getBookRealmInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyBookshelfBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val bookList =
            realm.where(BookObject::class.java).equalTo("uid", AuthHelper.getUid()).findAll()
        val adapter = MyBookshelfAdapter(requireContext(), bookList, true)

        // 画面のサイズを取得する
        val dm = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(dm)
        val width = dm.widthPixels / requireContext().resources.displayMetrics.density.toInt()

        // リサイクラービューの設定
        binding.bookListRecyclerView.layoutManager = GridLayoutManager(
            requireContext(),
            3,
            GridLayoutManager.VERTICAL,
            false
        )
        binding.bookListRecyclerView.addItemDecoration(GridItemDecoration(width, 3))
        binding.bookListRecyclerView.adapter = adapter
        binding.bookListRecyclerView.setHasFixedSize(true)

        adapter.setOnItemClickListener(object : MyBookshelfAdapter.OnItemClickListener {
            override fun onItemClickListener(view: View, position: Int, clickedId: String?) {
                val intent = Intent(view.context, DetailActivity::class.java)
                intent.putExtra(DetailActivity.BOOK_ID, clickedId)
                startActivity(intent)
            }
        })

        binding.spinnerDate.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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

        binding.addFab.setOnClickListener {
            val intent = Intent(view.context, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * 本棚を表示するメソッドです。
     * @param bookList realmの取得結果
     */
    private fun displayBookShelf(bookList: RealmResults<BookObject>) {
        val adapter = MyBookshelfAdapter(requireContext(), bookList, true)
        binding.bookListRecyclerView.adapter = adapter
        adapter.setOnItemClickListener(object : MyBookshelfAdapter.OnItemClickListener {
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            MyBookShelfFragment().apply {
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