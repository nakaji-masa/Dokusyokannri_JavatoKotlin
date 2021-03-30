package android.wings.websarva.dokusyokannrijavatokotlin.user.login.fragments

import android.os.Bundle
import android.text.*
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.wings.websarva.dokusyokannrijavatokotlin.R
import android.wings.websarva.dokusyokannrijavatokotlin.databinding.FragmentUserEmailBinding
import android.wings.websarva.dokusyokannrijavatokotlin.user.login.fragments.base.BaseAuthFragment
import java.util.regex.Pattern

class UserEmailFragment : BaseAuthFragment(), TextWatcher {
    private lateinit var paramType: String
    private var _binding: FragmentUserEmailBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            paramType = it.getString(TYPE_KEY, TYPE_REGISTER)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserEmailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setProgressBar(binding.loginProgressBar)
        binding.inputMailAddress.addTextChangedListener(this)
        binding.inputPassword.addTextChangedListener(this)

        if (paramType == TYPE_LOGIN) {
            binding.signInButton.text = getString(R.string.login)
        }

        binding.signInButton.setOnClickListener {
            val email = binding.inputMailAddress.text.toString()
            val password = binding.inputPassword.text.toString()
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
        binding.signInButton.isEnabled = isValidEmail() && isValidPassword()
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
        return Patterns.EMAIL_ADDRESS.matcher(binding.inputMailAddress.text.toString())
            .matches() && binding.inputMailAddress.text?.isNotBlank()!!
    }

    /**
     * パスワードが入力されているか、書式はあっているかを判定するメソッド
     * @return Boolean
     */
    private fun isValidPassword(): Boolean {
        val numberMatcher = Pattern.compile("[0-9]").matcher(binding.inputPassword.text.toString())
        val letterMatcher = Pattern.compile("[a-z]").matcher(binding.inputPassword.text.toString())
        return binding.inputPassword.text?.length!! >= 8 && letterMatcher.find() && numberMatcher.find()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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