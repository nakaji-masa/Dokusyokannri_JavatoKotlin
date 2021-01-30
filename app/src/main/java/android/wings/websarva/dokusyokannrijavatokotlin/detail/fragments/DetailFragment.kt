package android.wings.websarva.dokusyokannrijavatokotlin.detail.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.wings.websarva.dokusyokannrijavatokotlin.R
import android.wings.websarva.dokusyokannrijavatokotlin.activities.DetailActivity
import android.wings.websarva.dokusyokannrijavatokotlin.activities.RegisterActivity
import android.wings.websarva.dokusyokannrijavatokotlin.realm.`object`.BookObject
import android.wings.websarva.dokusyokannrijavatokotlin.realm.config.RealmConfigObject
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.FireStoreHelper
import android.wings.websarva.dokusyokannrijavatokotlin.utils.GlideHelper
import androidx.appcompat.app.AlertDialog
import io.realm.Realm
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.fragment_detail.*
import kotlinx.android.synthetic.main.fragment_detail.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DetailFragment : Fragment() {
    private lateinit var bookId: String
    private lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            bookId = it.getString(DetailActivity.BOOK_ID, "")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        realm = Realm.getInstance(RealmConfigObject.bookConfig)

        val book = realm.where<BookObject>().equalTo("id", bookId).findFirst()

        view.detailBookTitle.text = book?.title
        view.detailBookAuthor.text = getString(R.string.author, book?.author)
        view.detailBookDate.text = book?.date
        view.detailBookActionPlan.text = book?.actionPlan
        GlideHelper.viewBookImage(book?.imageUrl!!, view.detailBookImage)

        //本の内容を削除する
        deleteButton.setOnClickListener {

            AlertDialog.Builder(view.context)
                .setMessage(getString(R.string.dialog_delete_message))
                .setNegativeButton(getString(R.string.dialog_negative)) { dialog, which ->
                    dialog.dismiss()
                }
                .setPositiveButton(getString(R.string.dialog_positive)) { dialog, which ->
                    realm.executeTransaction {
                        realm.where<BookObject>().equalTo("id", bookId).findFirst()?.deleteFromRealm()
                    }

                    GlobalScope.launch {
                        FireStoreHelper.deleteDocument(bookId)
                    }

                    dialog.dismiss()
                    activity?.finish()
                }.show()
        }

        //本の内容を変更する
        updateButton.setOnClickListener {
            val intent = Intent(activity, RegisterActivity::class.java)
            intent.putExtra(RegisterActivity.INTENT_ID, bookId)
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
