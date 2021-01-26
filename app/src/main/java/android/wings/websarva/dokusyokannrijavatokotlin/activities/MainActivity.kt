package android.wings.websarva.dokusyokannrijavatokotlin.activities

import android.os.Bundle
import android.wings.websarva.dokusyokannrijavatokotlin.bookshelf.fragments.BookShelfFragment
import android.wings.websarva.dokusyokannrijavatokotlin.graph.fragments.GraphFragment
import android.wings.websarva.dokusyokannrijavatokotlin.R
import android.wings.websarva.dokusyokannrijavatokotlin.library.fragments.LibraryFragment
import android.wings.websarva.dokusyokannrijavatokotlin.settings.fragments.SettingsFragment
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.title = getString(R.string.title_bookshelf)

        val transaction = supportFragmentManager.beginTransaction()

        transaction.replace(R.id.detailContainer, BookShelfFragment.newInstance())

        transaction.commit()

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->

            when(item.itemId){
                R.id.book_read_item -> {
                    supportActionBar?.title = getString(R.string.title_bookshelf)
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.detailContainer, BookShelfFragment())
                        .commit()
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.graph_item -> {
                    supportActionBar?.title = getString(R.string.title_graph)
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.detailContainer, GraphFragment())
                        .commit()
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.library_item -> {
                    supportActionBar?.title = getString(R.string.title_post)
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.detailContainer, LibraryFragment())
                        .commit()
                    return@setOnNavigationItemSelectedListener true
                }


                R.id.settings_item -> {
                    supportActionBar?.title = getString(R.string.title_settings)
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.detailContainer, SettingsFragment())
                        .commit()
                    return@setOnNavigationItemSelectedListener true
                }

            }
            return@setOnNavigationItemSelectedListener false
        }
    }
}





