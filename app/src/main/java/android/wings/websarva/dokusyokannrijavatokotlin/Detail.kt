package android.wings.websarva.dokusyokannrijavatokotlin

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*

class Detail : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)


        val  id = intent?.getIntExtra("_id", 0)


        val helper = DataBaseHelper(this)

        val db: SQLiteDatabase = helper.getWritableDatabase()

        try {
            val sql =
                "SELECT bookname, deadline, bookNotice, bookActionplan, bookImage FROM BookList WHERE _id ="  + id
            val cursor = db.rawQuery(sql, null)

            var arrayByte = ByteArray(0)

            while (cursor.moveToNext()) {
                book_name_view.text = cursor.getString(cursor.getColumnIndex("bookName"))
                book_deadline_view.text = cursor.getString(cursor.getColumnIndex("deadline"))
                book_notice_view.text = cursor.getString(cursor.getColumnIndex("bookNotice"))
                book_actionplan_view.text = cursor.getString(cursor.getColumnIndex("bookActionplan"))
                arrayByte = cursor.getBlob(cursor.getColumnIndex("bookImage"))
                book_image_view.setImageBitmap(BitmapFactory.decodeByteArray(arrayByte, 0, arrayByte.size))
            }

        } finally {
            db.close()
        }

        backButton.setOnClickListener {
            finish()
        }

        ChangeButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)

            intent.putExtra("_id", id)

            startActivity(intent)
        }

    }
}
