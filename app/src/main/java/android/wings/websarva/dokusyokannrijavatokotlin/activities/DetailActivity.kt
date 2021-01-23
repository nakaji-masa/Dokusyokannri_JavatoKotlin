package android.wings.websarva.dokusyokannrijavatokotlin.activities

import android.os.Bundle
import android.view.MenuItem
import android.wings.websarva.dokusyokannrijavatokotlin.R
import android.wings.websarva.dokusyokannrijavatokotlin.detail.fragments.DetailSelectFragment
import androidx.appcompat.app.AppCompatActivity

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val id = intent.getStringExtra(BOOK_ID)

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
        const val ACTION_PLAN_ID = "action_id"
    }

}
