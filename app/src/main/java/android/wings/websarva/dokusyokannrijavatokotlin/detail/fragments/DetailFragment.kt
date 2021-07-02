package android.wings.websarva.dokusyokannrijavatokotlin.detail.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.wings.websarva.dokusyokannrijavatokotlin.R
import android.wings.websarva.dokusyokannrijavatokotlin.activities.RegisterActivity
import android.wings.websarva.dokusyokannrijavatokotlin.databinding.FragmentDetailBinding
import android.wings.websarva.dokusyokannrijavatokotlin.detail.fragments.base.BaseDetailFragment
import android.wings.websarva.dokusyokannrijavatokotlin.realm.`object`.BookObject
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.FireStoreHelper
import android.wings.websarva.dokusyokannrijavatokotlin.main.navigator.MainNavigator
import android.wings.websarva.dokusyokannrijavatokotlin.utils.GlideHelper
import androidx.appcompat.app.AlertDialog
import io.realm.RealmChangeListener
import io.realm.kotlin.where
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DetailFragment : BaseDetailFragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private val scope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // Viewに値を入れる
        setView()

        //本の内容を削除する
        binding.deleteButton.setOnClickListener {

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
                    scope.launch {
                        FireStoreHelper.deleteDocument(bookId!!)
                    }

                    // メイン画面の更新の合図
                    MainNavigator.setBookFlag()
                    dialog.dismiss()
                    requireActivity().finish()
                }.show()
        }

        //本の内容を変更する
        binding.updateButton.setOnClickListener {
            val intent = Intent(requireActivity(), RegisterActivity::class.java)
            bookObj?.let {
                intent.putExtra(RegisterActivity.INTENT_BOOK_OBJECT_ID, it.id)
            }
            requireActivity().startActivity(intent)
        }

        bookObj?.addChangeListener(RealmChangeListener<BookObject> {
            setView()
        })
    }

    /**
     * Viewに値を入れるメソッド
     */
    private fun setView() {
        bookObj?.let {
            binding.detailBookTitle.text = it.title
            binding.detailBookAuthor.text = getString(R.string.author, it.author)
            binding.detailBookDate.text = it.date
            binding.detailBookActionPlan.text = it.actionPlan
            GlideHelper.viewBookImage(it.imageUrl, binding.detailBookImage)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
