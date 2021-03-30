package android.wings.websarva.dokusyokannrijavatokotlin.user.login.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.wings.websarva.dokusyokannrijavatokotlin.R
import android.wings.websarva.dokusyokannrijavatokotlin.databinding.FragmentLoginBinding
import android.wings.websarva.dokusyokannrijavatokotlin.user.login.fragments.base.BaseAuthFragment
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.AuthHelper
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.OAuthProvider

class LoginFragment : BaseAuthFragment() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
        try {
            val account = task.getResult(ApiException::class.java)
            Log.d(TAG_GOOGLE, "firebaseAuthWithGoogle:" + account?.idToken)
            signInAccountWithGoogle(account?.idToken!!)

        } catch (e: ApiException) {
            Log.w(TAG_GOOGLE, "google sign in failed", e)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
        googleSignInClient = AuthHelper.getGoogleSignClient()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        requireActivity().actionBar?.hide()
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setProgressBar(binding.loginProgressBar)
        binding.googleRegisterView.setOnClickListener {
            googleSignIn()
        }

        binding.twitterRegisterView.setOnClickListener {
            twitterSignIn()
        }

        binding.mailRegisterView.setOnClickListener {
            moveToUserEmailFragment(UserEmailFragment.TYPE_REGISTER)
        }

        binding.mailLoginView.setOnClickListener {
            moveToUserEmailFragment(UserEmailFragment.TYPE_LOGIN)
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
        startForResult.launch(googleSignInClient.signInIntent)
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
                    hideProgressBar()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG_GOOGLE = "Google"
        const val TAG_TWITTER = "Twitter"

        @JvmStatic
        fun newInstance() =
            LoginFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}