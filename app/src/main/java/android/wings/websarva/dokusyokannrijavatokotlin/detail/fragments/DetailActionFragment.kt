package android.wings.websarva.dokusyokannrijavatokotlin.detail.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.wings.websarva.dokusyokannrijavatokotlin.activities.ActionRegisterActivity
import android.wings.websarva.dokusyokannrijavatokotlin.detail.DetailActionAdapter
import android.wings.websarva.dokusyokannrijavatokotlin.R
import android.wings.websarva.dokusyokannrijavatokotlin.realm.`object`.BookListObject
import android.wings.websarva.dokusyokannrijavatokotlin.realm.config.RealmConfigObject
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.realm.Realm
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.fragment_action_detail.*


class DetailActionFragment : Fragment() {
    private var id: Int? = null
    private lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            id = it.getInt("id")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_action_detail, container, false)
        realm = Realm.getInstance(RealmConfigObject.bookListConfig)
        val book = realm.where<BookListObject>().equalTo("id", id).findFirst()
        val actionList = book?.actionPlanDairy

        //アダプターをセットする。
        val adapter = DetailActionAdapter(actionList)

        //リサイクラービューの設定
        val recyclerView = view.findViewById<RecyclerView>(R.id.actionDetailRecyclerView)
        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(view.context)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        addActionButton.setOnClickListener {
            val intent = Intent(view.context, ActionRegisterActivity::class.java)
            intent.putExtra("id", id)
            startActivity(intent)
        }

    }

    companion object {

        @JvmStatic
        fun newInstance(id: Int) =
            DetailActionFragment().apply {
                arguments = Bundle().apply {
                    val fragment = DetailActionFragment()

                    val bundle = Bundle()

                    bundle.putInt("id", id)

                    fragment.arguments = bundle

                    return fragment
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
}
