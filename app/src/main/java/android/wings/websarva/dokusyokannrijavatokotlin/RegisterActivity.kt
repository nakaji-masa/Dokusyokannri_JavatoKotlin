package android.wings.websarva.dokusyokannrijavatokotlin

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.activity_register.*
import okhttp3.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class RegisterActivity : AppCompatActivity() {

    companion object {
        const val CAMERA_REQUEST_CODE = 1
        const val CAMERA_PERMISSION_REQUEST_CODE = 2
        const val BARCODE_PERMISSION_REQUEST_CODE = 3
        var isbn: String = ""
        var title: String = ""
        var imageUrl: String? = null
        lateinit var handler: Handler
        lateinit var imageView: ImageView
        lateinit var editText: EditText
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)


        handler = Handler()
        imageView = findViewById(R.id.book_image)
        editText = findViewById(R.id.book_name_input)
        val id = intent.getIntExtra("_id", -1)



        if (id != -1) {
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

        save_button.setOnClickListener {
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


                } finally {
                    db.close()
                }

                val intent = Intent(this, MainActivity::class.java)

                startActivity(intent)
            }
        }

        book_image.setOnClickListener {
            //ユーザーの端末にカメラ機能があるかどうかの確認
            //Intentに入っている機能を使いたい。(MediaStore.ACTION_IMAGE_CAPTURE)
            //resolveActivityで確認する
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).resolveActivity(packageManager)?.let {
                //nullが返されなければ、カメラを起動かパーミッションチェックを行う。
                if (checkCameraPermission()) {
                    takePicture()
                } else {
                    grantCameraPermission()
                }
            } ?: Toast.makeText(this, "カメラを扱うアプリがありません", Toast.LENGTH_LONG).show()
            //カメラ機能がなければトーストを表示する。
        }


        back_button.setOnClickListener {
            finish()
        }



        test_button.setOnClickListener {
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).resolveActivity(packageManager)?.let {
                if (checkCameraPermission()) {
                    takeBarCode()

                } else {
                    grantBarCodePermission()
                }

            } ?: Toast.makeText(this, "カメラを扱うアプリがありません", Toast.LENGTH_LONG).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            //カメラを使った場合の処理
            val imageBitmap = data?.extras?.get("data") as Bitmap
            book_image.setImageBitmap(imageBitmap)

        } else {
            //バーコードから来た場合
            val resultBarcode = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

            if (resultBarcode != null) {
                //nullだった場合。トーストの表示
                if (resultBarcode.contents == null) {
                    Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
                } else {
                    //バーコードスキャナーで受け取った値を代入する。ボタンのリスナー処理に戻る
                    isbn = resultBarcode.contents

                    //無事に取得できればGoogleBooksAPIに接続し本の画像とタイトルを取得
                    if (isbn != null) {
                        val url = "https://www.googleapis.com/books/v1/volumes?q=isbn:$isbn"

                        //okHttpクライアントの設定
                        val okHttpClient = OkHttpClient()

                        //リクエストの中に情報を入れ込む
                        val request = Request.Builder().url(url).build()

                        //Callバックの中に情報を入れ込む。そして、非同期で実装。
                        okHttpClient.newCall(request).enqueue(object : Callback {

                            override fun onFailure(call: Call, e: IOException) {
                                //失敗したときのログを出力
                                Log.d("failure API Response", e.localizedMessage)
                            }

                            override fun onResponse(call: Call, response: Response) {
                                //成功したときの命令
                                try {
                                    //jsonデータの全体取得
                                    val rootJson = JSONObject(response.body?.string())

                                    //"items"タグのデータを取得。配列でないといけない。
                                    val items = rootJson.getJSONArray("items")

                                    //bookNameとbookImageに取得した値を入れたいので、メインスレッドに戻る。reflectResultに後の処理を任せる。
                                    val reflectResult = ReflectResult(items)

                                    handler.post(reflectResult)

                                } catch (e: IOException) {
                                    e.printStackTrace()
                                }
                            }
                        })
                    }
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    //バーコードリーダー起動メソッド
    private fun takeBarCode() {
        val intentIntegrator = IntentIntegrator(this)
            .setBeepEnabled(false)
            .apply {
                captureActivity = Portrait::class.java
            }.initiateScan()
    }

    //カメラアプリの起動メソッド
    private fun takePicture() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            addCategory(Intent.CATEGORY_DEFAULT)
        }

        startActivityForResult(intent, CAMERA_REQUEST_CODE)
    }

    //カメラ機能のパーミッションを持っているかどうかの確認
    //持っていればtrueを返す。
    private fun checkCameraPermission() = PackageManager.PERMISSION_GRANTED ==
            ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.CAMERA)
    // Manifest.permission.CAMERAっていう許可とっているかな？

    //パーミッションを得るための関数
    private fun grantCameraPermission() =
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_REQUEST_CODE
        )

    //バーコードからパーミッションを得るためのメソッド
    private fun grantBarCodePermission() =
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            BARCODE_PERMISSION_REQUEST_CODE
        )


    //パーミッションを得たときにカメラの起動する関数。
    //取得の確認をするメソッド。
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                takePicture()
            }
        }

        //バーコードリクエストコードを持っていたらバーコードを起動する。
        if (requestCode == BARCODE_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                takeBarCode()
            }
        }
    }

    private class ReflectResult : Runnable {
        constructor(items: JSONArray) {
            try {
                for (i in 0 until items.length()) {
                    //itemsタグのi番目を取得
                    val item = items.getJSONObject(i)

                    //itemsタグの中のｖolumeInfoを取得
                    val volumeInfo = item.getJSONObject("volumeInfo")

                    //volumeInfo内のtitleタグを取得
                    title = volumeInfo.getString("title")

                    //volumeInfoからimageLinksを取得
                    val imageLinks = volumeInfo.getJSONObject("imageLinks")

                    //imageLinksからサムネイルをゲット
                    imageUrl = imageLinks.getString("thumbnail")

                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

        override fun run() {
            val imagedownload = ImageDownload()
            imagedownload.execute()
        }
    }

    class ImageDownload() : AsyncTask<String, String, Bitmap>() {

        override fun doInBackground(vararg p0: String?): Bitmap? {
            //URLで取得した画像を格納するためのメソッド
            var image: Bitmap? = null


            //MainActivityからurlを取得
            val url = URL(imageUrl)

            //インターネット接続するためのオブジェクトを作成
            val con = url.openConnection() as HttpURLConnection

            //接続の時間と、画像を読み込む時間を設定
            con.readTimeout = 10000
            con.connectTimeout = 10000

            //GETに設定。情報を取得するだけなので
            con.requestMethod = "GET"

            con.instanceFollowRedirects = false

            //ヘッダーの設定
            con.setRequestProperty("Accept-Language", "jp")

            try {
                //接続
                con.connect()

                //データを取得する
                val inst = con.inputStream

                //Bitamp型に直す
                val bitmap = BitmapFactory.decodeStream(inst)

                //変数imageに格納する
                image = bitmap

                //接続を切る
                inst.close()

            } catch (e: IOException) {
                Log.d("IOException", e.toString())
            }

            con.disconnect()
            return image
        }

        override fun onPostExecute(result: Bitmap?) {
            imageView.setImageBitmap(result)
            editText.setText(title)
        }
    }
}
