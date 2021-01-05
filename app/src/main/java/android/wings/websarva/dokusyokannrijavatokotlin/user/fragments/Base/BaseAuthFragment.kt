package android.wings.websarva.dokusyokannrijavatokotlin.user.fragments.Base

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import android.wings.websarva.dokusyokannrijavatokotlin.R
import android.wings.websarva.dokusyokannrijavatokotlin.activities.MainActivity
import android.wings.websarva.dokusyokannrijavatokotlin.user.fragments.UserProfileFragment
import android.wings.websarva.dokusyokannrijavatokotlin.utils.AuthHelper
import android.wings.websarva.dokusyokannrijavatokotlin.utils.FireStoreHelper
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
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

    fun displayFailedToast() {
        Toast.makeText(requireContext(), "登録に失敗しました。", Toast.LENGTH_LONG).show()
    }


    fun moveToNextView() {
        GlobalScope.launch {
            if (FireStoreHelper.hasUserDocument()) {
                moveToMainActivity()
            } else {
                moveToUserProfileRegisterFragment()
            }
        }
    }

    fun moveToUserProfileRegisterFragment() {
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.replace(R.id.userContainer, UserProfileFragment.newInstance())
        transaction?.commit()
        hideProgressBar()
    }

    fun moveToMainActivity() {
        val intent = Intent(requireActivity(), MainActivity::class.java)
        startActivity(intent)
        activity?.finish()
        hideProgressBar()
    }

    fun setProgressBar(bar: ProgressBar) {
        progressBar = bar
    }

    fun showProgressBar() {
        progressBar?.visibility = View.VISIBLE
    }

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

