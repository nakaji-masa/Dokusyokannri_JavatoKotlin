package android.wings.websarva.dokusyokannrijavatokotlin.user.fragments

import android.os.Bundle
import android.text.*
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.wings.websarva.dokusyokannrijavatokotlin.R
import android.wings.websarva.dokusyokannrijavatokotlin.user.fragments.base.BaseAuthFragment
import kotlinx.android.synthetic.main.fragment_user_email.view.*
import kotlinx.android.synthetic.main.fragment_user_email.view.loginProgressBar
import java.util.regex.Pattern

class UserEmailFragment : BaseAuthFragment(), TextWatcher {
    private lateinit var paramType: String
    private lateinit var inputEmail: EditText
    private lateinit var inputPassword: EditText
    private lateinit var signInButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            paramType = it.getString(TYPE_KEY, TYPE_REGISTER)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user_email, container, false)
        inputEmail = view.inputMailAddress
        inputPassword = view.inputPassword
        signInButton = view.signInButton
        setProgressBar(view.loginProgressBar)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        inputEmail.addTextChangedListener(this)
        inputPassword.addTextChangedListener(this)

        if (paramType == TYPE_LOGIN) {
            signInButton.text = getString(R.string.login)
        }

        view.signInButton.setOnClickListener {
            val email = inputEmail.text.toString()
            val password = inputPassword.text.toString()
            when (paramType) {
                TYPE_REGISTER -> {
                    createAccountWithEmailAndPassword(email, password)
                }

                TYPE_LOGIN -> {
                    loginAccountWithEmailAndPassword(email, password)
                }
            }
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        signInButton.isEnabled = isValidEmail() && isValidPassword()
    }

    override fun afterTextChanged(s: Editable?) {
    }

    /**
     * 新規登録するメソッド
     * @param email
     * @param password
     */
    private fun createAccountWithEmailAndPassword(email: String, password: String) {
        showProgressBar()
        Log.d(TAG_EMAIL, "createAccount:${email}")
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d(TAG_EMAIL, "emailCreateAccount: successful")
                    moveToUserProfileRegisterFragment()
                } else {
                    Log.d(TAG_EMAIL, "emailCreateAccount: failed", it.exception)
                    displayFailedToast()
                    hideProgressBar()
                }
            }
    }

    /**
     * ログインするメソッド
     * @param email
     * @param password
     */
    private fun loginAccountWithEmailAndPassword(email: String, password: String) {
        showProgressBar()
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    moveToMainActivity()
                } else {
                    Toast.makeText(requireActivity(), "メールアドレスまたはパスワードが違います。", Toast.LENGTH_LONG)
                        .show()
                }
            }
    }

    /**
     * メールアドレスが入力されているか、書式はあっているかを判定するメソッド
     * @return Boolean
     */
    private fun isValidEmail(): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(inputEmail.text)
            .matches() && inputEmail.text.isNotBlank()
    }

    /**
     * パスワードが入力されているか、書式はあっているかを判定するメソッド
     * @return Boolean
     */
    private fun isValidPassword(): Boolean {
        val numberMatcher = Pattern.compile("[0-9]").matcher(inputPassword.text)
        val letterMatcher = Pattern.compile("[a-z]").matcher(inputPassword.text)
        return inputPassword.text.length >= 8 && letterMatcher.find() && numberMatcher.find()
    }

    companion object {
        private const val TYPE_KEY = "type_key"
        private const val TAG_EMAIL = "email"
        const val TYPE_REGISTER = "register"
        const val TYPE_LOGIN = "login"


        @JvmStatic
        fun newInstance(type: String) =
            UserEmailFragment().apply {
                arguments = Bundle().apply {
                    putString(TYPE_KEY, type)
                }
            }
    }


}