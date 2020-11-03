package android.wings.websarva.dokusyokannrijavatokotlin

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import android.wings.websarva.dokusyokannrijavatokotlin.R.id.book_read_item
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_main.*
import java.nio.file.Files.find


class MainActivity : AppCompatActivity() {

    private lateinit var realm : Realm
    
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

            }
            return@setOnNavigationItemSelectedListener false
        }


    }
}





