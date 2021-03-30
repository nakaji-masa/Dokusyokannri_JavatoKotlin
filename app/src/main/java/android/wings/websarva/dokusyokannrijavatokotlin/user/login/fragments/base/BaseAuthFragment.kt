package android.wings.websarva.dokusyokannrijavatokotlin.user.login.fragments.base

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import android.wings.websarva.dokusyokannrijavatokotlin.R
import android.wings.websarva.dokusyokannrijavatokotlin.activities.MainActivity
import android.wings.websarva.dokusyokannrijavatokotlin.user.login.fragments.UserProfileFragment
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.AuthHelper
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.FireStoreHelper
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

open class BaseAuthFragment : Fragment() {
    private var progressBar: ProgressBar? = null
    lateinit var mAuth: FirebaseAuth
    lateinit var userCollection: CollectionReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        mAuth = AuthHelper.getFirebaseAuth()
        userCollection = FirebaseFirestore.getInstance().collection(USER_COLLECTION_PATH)
    }

    /**
     * トーストを表示するメソッド
     */
    fun displayFailedToast() {
        Toast.makeText(requireContext(), "登録に失敗しました。", Toast.LENGTH_LONG).show()
    }


    /**
     * プロフィール情報があるかないかで遷移する画面を変えるメソッド
     */
    fun moveToNextView() {
        GlobalScope.launch(Dispatchers.Main) {
            if (FireStoreHelper.hasUserDocument()) {
                moveToMainActivity()
            } else {
                moveToUserProfileRegisterFragment()
            }
        }
    }

    /**
     * プロフィール入力画面に遷移するメソッド
     */
    fun moveToUserProfileRegisterFragment() {
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.replace(R.id.userContainer, UserProfileFragment.newInstance(UserProfileFragment.NEW_MODE))
        transaction?.commit()
        hideProgressBar()
    }

    /**
     * メインの画面に遷移するメソッド
     */
    fun moveToMainActivity() {
        val intent = Intent(requireActivity(), MainActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
        hideProgressBar()
    }

    /**
     * フィールドのprogressBarをセットするメソッド
     * @param bar ProgressBar
     */
    fun setProgressBar(bar: ProgressBar) {
        progressBar = bar
    }

    /**
     * ProgressBarを表示するメソッド
     */
    fun showProgressBar() {
        progressBar?.visibility = View.VISIBLE
    }

    /**
     * ProgressBarを非表示にするメソッド
     */
    fun hideProgressBar() {
        progressBar?.visibility = View.INVISIBLE
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> {
                activity?.onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val USER_COLLECTION_PATH = "user"
    }

}

