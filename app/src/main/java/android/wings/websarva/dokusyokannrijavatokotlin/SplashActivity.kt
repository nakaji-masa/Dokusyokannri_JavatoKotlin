package android.wings.websarva.dokusyokannrijavatokotlin

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.wings.websarva.dokusyokannrijavatokotlin.activities.MainActivity
import android.wings.websarva.dokusyokannrijavatokotlin.user.UserActivity
import android.wings.websarva.dokusyokannrijavatokotlin.utils.AuthHelper
import android.wings.websarva.dokusyokannrijavatokotlin.utils.FireStoreHelper
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.lang.Runnable
import java.nio.channels.AlreadyBoundException

class SplashActivity : AppCompatActivity() {
    private val fireStore = FirebaseFirestore.getInstance()
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
                        intent = Intent(this@SplashActivity, UserActivity::class.java)
                        intent.putExtra(ALREADY_REGISTERED_KEY, true)
                    }
                } else {
                    intent = Intent(this@SplashActivity, UserActivity::class.java)
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