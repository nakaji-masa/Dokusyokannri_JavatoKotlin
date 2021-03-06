package android.wings.websarva.dokusyokannrijavatokotlin.book.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.wings.websarva.dokusyokannrijavatokotlin.R
import android.wings.websarva.dokusyokannrijavatokotlin.book.fragments.PostForBookFragment
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.model.BookHelper
import androidx.fragment.app.FragmentActivity

class PostForBookActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_for_book)

        val book = intent.getParcelableExtra<BookHelper>(BOOK_DATA_KEY)

        supportActionBar?.title = book?.title
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.postForBookContainer, PostForBookFragment.newInstance())
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
        return when(item.itemId) {
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
        const val BOOK_DATA_KEY = "book_data_key"
        @JvmStatic
        fun moveToPostForBookActivity(activity: FragmentActivity, book: BookHelper) {
            val intent = Intent(activity, PostForBookActivity::class.java)
            intent.putExtra(BOOK_DATA_KEY, book)
            activity.startActivity(intent)
        }
    }
}