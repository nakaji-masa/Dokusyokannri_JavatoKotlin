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
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.booklist_cell.*
import java.nio.file.Files.find

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)




        //DataBaseHelperオブジェクトをインスタンス化
        val helper = DataBaseHelper(this)

        //sqLiteDatabaseからhelperを取得
        val sqLiteDatabase: SQLiteDatabase = helper.getReadableDatabase()

        //Bookデータベースから値を取得。結果をcursorへ代入する。
        val cursor = sqLiteDatabase.query("BookList", arrayOf("_id", "bookName", "bookImage"), null, null, null, null, null)

        //corsorの値をBaseadapterに格納
        val adapter: BaseAdapter = CursorAdapter(this, R.layout.booklist_cell, cursor, arrayOf("_id", "bookName", "bookImage"), intArrayOf(
            R.id.book_list_id, R.id.book_list_name, R.id.book_list_Image))

        //listViewへ格納
        bookList.adapter = adapter



        intentBookRegisterButton.setOnClickListener(View.OnClickListener {

            val intent = Intent(this, RegisterActivity::class.java)

            startActivity(intent)

        })

        bookList.setOnItemClickListener { adapterView, view, i, l ->

            val const = view as ConstraintLayout

            val id = const.findViewById<TextView>(R.id.book_list_id).text.toString().toInt()


            val intent = Intent(this, Detail::class.java)

            intent.putExtra("_id", id)

            startActivity(intent)
        }
    }
}





