package android.wings.websarva.dokusyokannrijavatokotlin.detail.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.wings.websarva.dokusyokannrijavatokotlin.detail.ActionDiaryAdapter
import android.wings.websarva.dokusyokannrijavatokotlin.R
import android.wings.websarva.dokusyokannrijavatokotlin.activities.DetailActivity
import android.wings.websarva.dokusyokannrijavatokotlin.realm.`object`.BookObject
import android.wings.websarva.dokusyokannrijavatokotlin.realm.config.RealmConfigObject
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.realm.Realm
import io.realm.Sort
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.fragment_action_dairy.view.*

interface OnActionDairyFragmentListener {
    fun onItemClicked(bookId: String, actionId: String)
    fun onAddButtonClicked(id: String)
}
class ActionDairyFragment : Fragment() {
    private lateinit var bookId: String
    private lateinit var realm: Realm
    var onActionDairyFragmentListener: OnActionDairyFragmentListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            bookId = it.getString(DetailActivity.BOOK_ID, "")
        }
        realm = Realm.getInstance(RealmConfigObject.bookListConfig)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_action_dairy, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val book = realm.where<BookObject>().equalTo("id", bookId).findFirst()
        val actionList = book?.actionPlanDairy?.sort("date", Sort.DESCENDING)

        //アダプターをセットする。
        val adapter = ActionDiaryAdapter(actionList)
        adapter.setItemClickListener(object : ActionDiaryAdapter.OnItemClickListener {
            override fun onItemClickListener(view: View, position: Int, clickedId: String) {
                onActionDairyFragmentListener?.onItemClicked(bookId, clickedId)
            }
        })

        //リサイクラービューの設定
        val recyclerView = view.findViewById<RecyclerView>(R.id.actionDetailRecyclerView)
        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(view.context)

        view.addActionButton.setOnClickListener {
            onActionDairyFragmentListener?.onAddButtonClicked(bookId)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    companion object {
        @JvmStatic
        fun newInstance(id: String?) =
            ActionDairyFragment().apply {
                arguments = Bundle().apply {
                    putString(DetailActivity.BOOK_ID, id)
                }
            }
    }

}
