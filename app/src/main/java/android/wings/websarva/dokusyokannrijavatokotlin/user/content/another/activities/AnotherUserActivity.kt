package android.wings.websarva.dokusyokannrijavatokotlin.user.content.another.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.wings.websarva.dokusyokannrijavatokotlin.R
import android.wings.websarva.dokusyokannrijavatokotlin.user.content.another.fragments.AnotherUserFragment
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.model.UserInfoHelper
import androidx.fragment.app.FragmentActivity

class AnotherUserActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_another_user)

        val userInfo = intent.getParcelableExtra<UserInfoHelper>(ANOTHER_USER_KEY)

        supportActionBar?.title = userInfo.userName
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.anotherUserContainer, AnotherUserFragment.newInstance())
        transaction.commit()

    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 0) {
            finish()
        } else {
            supportFragmentManager.popBackStack()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    companion object {
        const val ANOTHER_USER_KEY = "another_user_key"

        /**
         * AnotherUserActivityへ遷移させるメソッドです。
         * @param activity 現在のアクティビティー
         * @param user ユーザー情報
         */
        @JvmStatic
        fun moveToAnotherUserActivity(activity: FragmentActivity?, user: UserInfoHelper) {
            val intent = Intent(activity, AnotherUserActivity::class.java)
            intent.putExtra(ANOTHER_USER_KEY, user)
            activity?.startActivity(intent)
        }
    }
}