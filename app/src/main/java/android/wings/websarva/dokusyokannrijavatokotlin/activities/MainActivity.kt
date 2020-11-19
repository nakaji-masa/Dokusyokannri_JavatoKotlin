package android.wings.websarva.dokusyokannrijavatokotlin.activities

import android.os.Bundle
import android.wings.websarva.dokusyokannrijavatokotlin.booklist.fragments.BookListFragment
import android.wings.websarva.dokusyokannrijavatokotlin.graph.fragments.GraphFragment
import android.wings.websarva.dokusyokannrijavatokotlin.R
import android.wings.websarva.dokusyokannrijavatokotlin.library.fragments.LibraryFragment
import android.wings.websarva.dokusyokannrijavatokotlin.settings.fragments.SettingsFragment
import androidx.appcompat.app.AppCompatActivity
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val fragment = BookListFragment()

        val transaction = supportFragmentManager.beginTransaction()

        transaction.replace(R.id.container, fragment)

        transaction.commit()

        bottom_navigationView.setOnNavigationItemSelectedListener {item ->

            when(item.itemId){
                R.id.book_read_item -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, BookListFragment())
                        .commit()
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.graph_item -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, GraphFragment())
                        .commit()
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.settings_item -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, SettingsFragment())
                        .commit()
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.library_item -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, LibraryFragment())
                        .commit()
                    return@setOnNavigationItemSelectedListener true
                }

            }
            return@setOnNavigationItemSelectedListener false
        }


    }
}





