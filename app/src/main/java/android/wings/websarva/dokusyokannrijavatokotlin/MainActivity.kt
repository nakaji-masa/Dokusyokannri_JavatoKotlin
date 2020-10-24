package android.wings.websarva.dokusyokannrijavatokotlin

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.LinearLayoutManager
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_main.*
import java.nio.file.Files.find


class MainActivity : AppCompatActivity() {

    private lateinit var realm : Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //アクションバーのセット
        setSupportActionBar(parent_toolbar)


        realm = Realm.getDefaultInstance()

        val bookList = realm.where(BookListObject::class.java).findAll()

        val adapter = BookListAdapterMain(this, bookList, true)

        //レイアウトマネージャーなどの設定
        book_recyclerview.setHasFixedSize(true)
        book_recyclerview.layoutManager = LinearLayoutManager(this)
        book_recyclerview.adapter = adapter

        //リスナーの設定
        adapter.setOnItemClickListener(object :  BookListAdapterMain.OnItemClickListener {
            override fun onItemClickListener(view: View, position: Int, clickedId: Int?) {
                val intent = Intent(view.context, Detail::class.java)
                intent.putExtra("id", clickedId)
                startActivity(intent)
            }
        })


        add_fab.setOnClickListener(View.OnClickListener {

            val intent = Intent(this, RegisterActivity::class.java)

            startActivity(intent)

        })

    }
}





