package android.wings.websarva.dokusyokannrijavatokotlin.user

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.wings.websarva.dokusyokannrijavatokotlin.R
import android.wings.websarva.dokusyokannrijavatokotlin.SplashActivity
import android.wings.websarva.dokusyokannrijavatokotlin.user.fragments.LoginFragment
import android.wings.websarva.dokusyokannrijavatokotlin.user.fragments.UserProfileFragment
import android.wings.websarva.dokusyokannrijavatokotlin.utils.AuthHelper

class UserActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)
        val alreadyRegistered = intent.getBooleanExtra(SplashActivity.ALREADY_REGISTERED_KEY, false)
        val transaction = supportFragmentManager.beginTransaction()
        if (alreadyRegistered) {
            transaction.add(R.id.userContainer, UserProfileFragment.newInstance())
        } else {
            transaction.add(R.id.userContainer, LoginFragment.newInstance())
        }
        transaction.commit()
    }
}