package android.wings.websarva.dokusyokannrijavatokotlin

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.wings.websarva.dokusyokannrijavatokotlin.activities.MainActivity
import android.wings.websarva.dokusyokannrijavatokotlin.user.login.LoginActivity
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.AuthHelper
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.FireStoreHelper
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import java.lang.Runnable

class SplashActivity : AppCompatActivity() {
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        supportActionBar?.hide()
        handler = Handler()
        runnable = Runnable {
            GlobalScope.launch {
                val intent: Intent
                if(AuthHelper.isLogin()) {
                    if (FireStoreHelper.hasUserDocument()) {
                        intent = Intent(this@SplashActivity, MainActivity::class.java)
                    } else {
                        intent = Intent(this@SplashActivity, LoginActivity::class.java)
                        intent.putExtra(ALREADY_REGISTERED_KEY, true)
                    }
                } else {
                    intent = Intent(this@SplashActivity, LoginActivity::class.java)
                }
                startActivity(intent)
                finish()
            }
        }
        handler.postDelayed(runnable, 2000)
    }

    companion object {
        const val ALREADY_REGISTERED_KEY = "registered"
    }

}