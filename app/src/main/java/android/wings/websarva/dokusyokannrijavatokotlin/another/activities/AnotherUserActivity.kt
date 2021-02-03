package android.wings.websarva.dokusyokannrijavatokotlin.another.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.wings.websarva.dokusyokannrijavatokotlin.R
import android.wings.websarva.dokusyokannrijavatokotlin.another.fragments.ContentsSelectFragment
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.model.UserInfoHelper
import android.wings.websarva.dokusyokannrijavatokotlin.utils.GlideHelper
import androidx.fragment.app.FragmentActivity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_another_user.*

class AnotherUserActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_another_user)

        val uid = intent.extras?.getString(ANOTHER_UID_KEY, "")
        val userJson = intent.extras?.getString(ANOTHER_USER_KEY, "")
        val userInfo = Gson().fromJson<UserInfoHelper>(userJson, UserInfoHelper::class.java)

        supportActionBar?.title = userInfo.userName
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        GlideHelper.viewUserImage(userInfo.userImageUrl, anotherUserImage)
        anotherUserName.text = userInfo.userName
        anotherUserIntroduction.text = userInfo.introduction

        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.anotherUserContainer,ContentsSelectFragment.newInstance(uid, userJson))
        transaction.commit()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            finish()
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
        const val ANOTHER_UID_KEY = "another_uid_key"

        /**
         * AnotherUserActivityへ遷移させるメソッドです。
         * @param activity 現在のアクティビティー
         * @param uid ユーザーID
         * @param userJson ユーザー情報
         */
        @JvmStatic
        fun moveToAnotherUserActivity(activity: FragmentActivity?, uid: String, userJson: String) {
            val intent = Intent(activity, AnotherUserActivity::class.java)
            intent.putExtra(ANOTHER_UID_KEY, uid)
            intent.putExtra(ANOTHER_USER_KEY, userJson)
            activity?.startActivity(intent)
        }
    }
}