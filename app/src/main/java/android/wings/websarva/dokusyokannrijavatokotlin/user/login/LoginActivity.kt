package android.wings.websarva.dokusyokannrijavatokotlin.user.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.wings.websarva.dokusyokannrijavatokotlin.R
import android.wings.websarva.dokusyokannrijavatokotlin.SplashActivity
import android.wings.websarva.dokusyokannrijavatokotlin.user.login.fragments.LoginFragment
import android.wings.websarva.dokusyokannrijavatokotlin.user.login.fragments.UserProfileFragment

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val alreadyRegistered = intent.getBooleanExtra(SplashActivity.ALREADY_REGISTERED_KEY, false)
        val transaction = supportFragmentManager.beginTransaction()
        if (alreadyRegistered) {
            transaction.add(R.id.userContainer, UserProfileFragment.newInstance(UserProfileFragment.NEW_MODE))
        } else {
            transaction.add(R.id.userContainer, LoginFragment.newInstance())
        }
        transaction.commit()
    }
}