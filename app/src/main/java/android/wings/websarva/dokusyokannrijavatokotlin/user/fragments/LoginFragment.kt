package android.wings.websarva.dokusyokannrijavatokotlin.user.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.wings.websarva.dokusyokannrijavatokotlin.R
import android.wings.websarva.dokusyokannrijavatokotlin.user.fragments.Base.BaseAuthFragment
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.AuthHelper
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.OAuthProvider
import kotlinx.android.synthetic.main.fragment_login.view.*

class LoginFragment : BaseAuthFragment() {

    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
        googleSignInClient = AuthHelper.getGoogleSignClient()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.actionBar?.hide()
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setProgressBar(view.loginProgressBar)
        view.googleRegisterView.setOnClickListener {
            googleSignIn()
        }

        view.twitterRegisterView.setOnClickListener {
            twitterSignIn()
        }

        view.mailRegisterView.setOnClickListener {
            moveToUserEmailFragment(UserEmailFragment.TYPE_REGISTER)
        }

        view.mailLoginView.setOnClickListener {
            moveToUserEmailFragment(UserEmailFragment.TYPE_LOGIN)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                Log.d(TAG_GOOGLE, "firebaseAuthWithGoogle:" + account?.idToken)
                signInAccountWithGoogle(account?.idToken!!)

            } catch (e: ApiException) {
                Log.w(TAG_GOOGLE, "google sign in failed", e)
            }
        }
    }

    /**
     * Googleにサインインするメソッド
     * @param idToken
     */
    private fun signInAccountWithGoogle(idToken: String) {
        showProgressBar()
        val credential: AuthCredential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d(TAG_GOOGLE, "signInGoogle: successful")
                    moveToNextView()
                } else {
                    Log.d(TAG_GOOGLE, "signInGoogle: failed")
                    displayFailedToast()
                }
            }
    }

    /**
     * サインインするGoogleアカウントを表示するメソッド
     */
    private fun googleSignIn() {
        val googleSignInIntent = googleSignInClient.signInIntent
        startActivityForResult(googleSignInIntent, RC_GOOGLE_SIGN_IN)
    }

    /**
     * twitterでサインインするためのメソッド
     */
    private fun twitterSignIn() {
        showProgressBar()
        val provider = OAuthProvider.newBuilder("twitter.com")
        mAuth.startActivityForSignInWithProvider(requireActivity(), provider.build())
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d(TAG_TWITTER, "signInTwitter: successful")
                    moveToNextView()
                } else {
                    Log.d(TAG_TWITTER, "signInTwitter: failed")
                    displayFailedToast()
                }
            }
    }

    /**
     * メールアドレスで登録・ログインするフラグメントへ遷移するメソッド
     */
    private fun moveToUserEmailFragment(type: String) {
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.replace(R.id.userContainer, UserEmailFragment.newInstance(type))
        transaction?.addToBackStack(null)
        transaction?.commit()
    }

    companion object {
        const val TAG_GOOGLE = "Google"
        const val TAG_TWITTER = "Twitter"
        const val RC_GOOGLE_SIGN_IN = 1

        @JvmStatic
        fun newInstance() =
            LoginFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}