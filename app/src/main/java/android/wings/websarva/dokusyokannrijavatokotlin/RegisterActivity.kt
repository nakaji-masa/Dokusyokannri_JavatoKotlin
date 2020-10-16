package android.wings.websarva.dokusyokannrijavatokotlin

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_register.*
import java.io.ByteArrayOutputStream

class RegisterActivity : AppCompatActivity() {


    val REQUEST_IMAGE_CAPTURE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val id = intent.getIntExtra("_id", -1)



        if(id != -1) {
            val helper = DataBaseHelper(this)

            val db = helper.writableDatabase

            try {
                val sql =
                    "SELECT bookName, deadline, bookNotice, bookActionPlan, bookImage FROM BookList WHERE _id =$id"

                val cursor = db.rawQuery(sql, null)

                var arrayByte = ByteArray(0)

                while (cursor.moveToNext()) {
                    book_name_input.setText(cursor.getString(cursor.getColumnIndex("bookName")))
                    book_deadline_input.setText(cursor.getString(cursor.getColumnIndex("deadline")))
                    book_notice_input.setText(cursor.getString(cursor.getColumnIndex("bookNotice")))
                    book_actionPlan_input.setText(cursor.getString(cursor.getColumnIndex("bookActionPlan")))
                    arrayByte = cursor.getBlob(cursor.getColumnIndex("bookImage"))
                    book_image.setImageBitmap(
                        BitmapFactory.decodeByteArray(
                            arrayByte,
                            0,
                            arrayByte.size
                        )
                    )
                }

            } finally {
                db.close()
            }

        }

        save_button.setOnClickListener{
            if (id == -1) {
                if (book_name_input.text.toString() == "" || book_deadline_input.text.toString() == "" ||
                    book_notice_input.text.toString() == "" || book_actionPlan_input.text.toString() == ""
                ) {
                    Toast.makeText(this, "未入力の項目があります。", Toast.LENGTH_LONG).show()
                } else {
                    //DataBaseHelperクラスのインスタンスを作成
                    val helper = DataBaseHelper(this)

                    //データーベースに書き込みを加える
                    val db = helper.writableDatabase

                    try {
                        val sqlInsert =
                            "INSERT INTO BookList (bookName, deadline, bookNotice, bookActionPlan, bookImage) VALUES(?, ?, ?, ?, ?)"

                        //レコードの追加
                        val stmt = db.compileStatement(sqlInsert)

                        //保存したい値を格納
                        stmt.bindString(1, book_name_input.text.toString())
                        stmt.bindString(2, book_deadline_input.text.toString())
                        stmt.bindString(3, book_notice_input.text.toString())
                        stmt.bindString(4, book_actionPlan_input.text.toString())

                        //bitmap型からbyteArray型に変換
                        val byteArrayOutputStream = ByteArrayOutputStream()
                        var bitmap = (book_image.drawable as BitmapDrawable).bitmap
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)

                        //保存する値を格納
                        stmt.bindBlob(5, byteArrayOutputStream.toByteArray())

                        //sql文の実行
                        stmt.executeInsert()

                    } finally {
                        db.close()
                    }
                    val intent = Intent(this, MainActivity::class.java)

                    startActivity(intent)

                }

            } else {
                val helper = DataBaseHelper(this)

                val db = helper.writableDatabase

                try {
                    val sql =
                        "UPDATE BookList SET bookName = ?, deadline = ?, bookNotice = ?, bookActionPlan = ?, bookImage = ?  WHERE _id = ?"

                    val stmt = db.compileStatement(sql)

                    stmt.bindString(1, book_name_input.text.toString())
                    println(book_name_input.text.toString())
                    stmt.bindString(2, book_deadline_input.text.toString())
                    stmt.bindString(3, book_notice_input.text.toString())
                    stmt.bindString(4, book_actionPlan_input.text.toString())

                    val byteArrayOutputStream = ByteArrayOutputStream()
                    val bitmap = (book_image.drawable as BitmapDrawable).bitmap
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
                    stmt.bindBlob(5, byteArrayOutputStream.toByteArray())
                    stmt.bindLong(6, id.toLong())

                    stmt.executeUpdateDelete()


                }finally {
                    db.close()
                }

                val intent = Intent(this, MainActivity::class.java)

                startActivity(intent)
            }


        }





        book_image.setOnClickListener {
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also {takePictureIntent ->
                takePictureIntent.resolveActivity(packageManager)?.also {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }

        back_button.setOnClickListener {
            finish()
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            val imageBitmap = data?.extras?.get("data") as Bitmap
            book_image.setImageBitmap(imageBitmap)
        }
    }
}
