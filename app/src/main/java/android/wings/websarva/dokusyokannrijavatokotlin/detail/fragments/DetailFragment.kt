package android.wings.websarva.dokusyokannrijavatokotlin.detail.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.wings.websarva.dokusyokannrijavatokotlin.R
import android.wings.websarva.dokusyokannrijavatokotlin.activities.DetailActivity
import android.wings.websarva.dokusyokannrijavatokotlin.activities.MainActivity
import android.wings.websarva.dokusyokannrijavatokotlin.activities.RegisterActivity
import android.wings.websarva.dokusyokannrijavatokotlin.realm.`object`.BookObject
import android.wings.websarva.dokusyokannrijavatokotlin.realm.config.RealmConfigObject
import android.wings.websarva.dokusyokannrijavatokotlin.utils.GlideHelper
import androidx.appcompat.app.AlertDialog
import io.realm.Realm
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.fragment_detail.*
import kotlinx.android.synthetic.main.fragment_detail.view.*

class DetailFragment : Fragment() {
    private var id: String? = null
    private lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            id = it.getString(DetailActivity.BOOK_ID, "")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        realm = Realm.getInstance(RealmConfigObject.bookListConfig)

        val book = realm.where<BookObject>().equalTo("id", id).findFirst()

        view.detailBookTitle.text = book?.title
        view.detailBookAuthor.text = getString(R.string.author, book?.author)
        view.detailBookDate.text = book?.date
        view.detailBookActionPlan.text = book?.actionPlan
        GlideHelper.viewGlide(book?.imageUrl!!, view.detailBookImage)

        //本の内容を削除する
        deleteButton.setOnClickListener {
            realm.executeTransaction {
                realm.where<BookObject>().equalTo("id", id).findFirst()?.deleteFromRealm()
            }

            AlertDialog.Builder(view.context)
                .setMessage("削除しました。")
                .setPositiveButton("OK") { dialog, which ->
                    activity?.supportFragmentManager?.popBackStack()
                }.show()
        }

        //本の内容を変更する
        updateButton.setOnClickListener {
            val intent = Intent(activity, RegisterActivity::class.java)
            intent.putExtra(RegisterActivity.INTENT_ID, id)
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    companion object {
        @JvmStatic
        fun newInstance(id: String?) =
            DetailFragment().apply {
                arguments = Bundle().apply {
                    putString(DetailActivity.BOOK_ID, id)
                }
            }

    }
}
