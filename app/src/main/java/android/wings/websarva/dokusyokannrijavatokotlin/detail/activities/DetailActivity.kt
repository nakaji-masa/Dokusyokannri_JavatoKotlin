package android.wings.websarva.dokusyokannrijavatokotlin.detail.activities

import android.os.Bundle
import android.view.MenuItem
import android.wings.websarva.dokusyokannrijavatokotlin.R
import android.wings.websarva.dokusyokannrijavatokotlin.detail.fragments.DetailSelectFragment
import android.wings.websarva.dokusyokannrijavatokotlin.realm.`object`.BookObject
import android.wings.websarva.dokusyokannrijavatokotlin.realm.manager.RealmManager
import androidx.appcompat.app.AppCompatActivity
import io.realm.Realm

class DetailActivity : AppCompatActivity() {

    private lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        realm = RealmManager.getBookRealmInstance()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        // 本のタイトルを取得する
        val id = intent.getStringExtra(BOOK_ID)
        val title = realm.where(BookObject::class.java).equalTo("id", id).findFirst()?.title
        supportActionBar?.title = title

        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.detailContainer, DetailSelectFragment.newInstance(id))
        transaction.commit()
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

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 0) {
            finish()
        } else {
            supportFragmentManager.popBackStack()
        }
    }

    companion object {
        const val BOOK_ID = "book_id"
    }

}
