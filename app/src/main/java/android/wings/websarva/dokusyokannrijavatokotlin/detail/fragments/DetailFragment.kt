package android.wings.websarva.dokusyokannrijavatokotlin.detail.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.wings.websarva.dokusyokannrijavatokotlin.R
import android.wings.websarva.dokusyokannrijavatokotlin.activities.RegisterActivity
import android.wings.websarva.dokusyokannrijavatokotlin.detail.fragments.base.BaseDetailFragment
import android.wings.websarva.dokusyokannrijavatokotlin.realm.`object`.BookObject
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.FireStoreHelper
import android.wings.websarva.dokusyokannrijavatokotlin.main.navigator.MainNavigator
import android.wings.websarva.dokusyokannrijavatokotlin.utils.GlideHelper
import androidx.appcompat.app.AlertDialog
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.fragment_detail.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DetailFragment : BaseDetailFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // Viewに値を入れる
        setView()

        //本の内容を削除する
        deleteButton.setOnClickListener {

            AlertDialog.Builder(view.context)
                .setMessage(getString(R.string.dialog_delete_message))
                .setNegativeButton(getString(R.string.dialog_negative)) { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton(getString(R.string.dialog_positive)) { dialog, _ ->
                    realm.executeTransaction {
                        realm.where<BookObject>().equalTo("id", bookId).findFirst()
                            ?.deleteFromRealm()
                    }

                    // fireStoreから削除する
                    GlobalScope.launch {
                        FireStoreHelper.deleteDocument(bookId!!)
                    }

                    // メイン画面の更新の合図
                    MainNavigator.setBookFlag()
                    dialog.dismiss()
                    requireActivity().finish()
                }.show()
        }

        //本の内容を変更する
        updateButton.setOnClickListener {
            val intent = Intent(requireActivity(), RegisterActivity::class.java)
            bookObj?.let {
                intent.putExtra(RegisterActivity.INTENT_BOOK_OBJECT_ID, it.id)
            }
            requireActivity().startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        setView()
    }

    /**
     * Viewに値を入れるメソッド
     */
    private fun setView() {
        bookObj?.let {
            detailBookTitle.text = it.title
            detailBookAuthor.text = getString(R.string.author, it.author)
            detailBookDate.text = it.date
            detailBookActionPlan.text = it.actionPlan
            GlideHelper.viewBookImage(it.imageUrl, detailBookImage)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            DetailFragment().apply {
                arguments = Bundle().apply {
                }
            }

    }
}
