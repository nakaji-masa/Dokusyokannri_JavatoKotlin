package android.wings.websarva.dokusyokannrijavatokotlin.activities

import android.Manifest
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
import android.view.MenuItem
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import android.wings.websarva.dokusyokannrijavatokotlin.utils.GetDateObject
import android.wings.websarva.dokusyokannrijavatokotlin.views.PortraitActivity
import android.wings.websarva.dokusyokannrijavatokotlin.R
import android.wings.websarva.dokusyokannrijavatokotlin.realm.`object`.BookListObject
import android.wings.websarva.dokusyokannrijavatokotlin.realm.`object`.GraphObject
import android.wings.websarva.dokusyokannrijavatokotlin.realm.`object`.GraphYearObject
import android.wings.websarva.dokusyokannrijavatokotlin.realm.config.RealmConfigObject
import android.wings.websarva.dokusyokannrijavatokotlin.register.BookHelper
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.zxing.integration.android.IntentIntegrator
import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_register.*
import okhttp3.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.Calendar.*

@Suppress(
    "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS",
    "RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS"
)
class
RegisterActivity : AppCompatActivity() {

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

    private lateinit var bookListRealm: Realm
    private lateinit var graphRealm: Realm
    private var firebaseMaxId: Long = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        setTitle(R.string.registerButton)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        bookListRealm = Realm.getInstance(RealmConfigObject.bookListConfig)
        graphRealm = Realm.getInstance(RealmConfigObject.graphConfig)

        handler = Handler()
        imageView = findViewById(R.id.book_image)
        editText = findViewById(R.id.book_title_input)

        //日付を取得
        val dateEditText = findViewById<EditText>(R.id.book_date_input)
        dateEditText.setText(GetDateObject.getToday())

        //日付の入力不可
        dateEditText.isEnabled = false

        val id = intent.getIntExtra("id", -1)

        if (id != -1) {
            val book = bookListRealm.where<BookListObject>().equalTo("id", id).findFirst()
            book_title_input.setText(book?.title)
            book_date_input.setText(book?.date)
            book_notice_input.setText(book?.notice)
            book_actionPlan_input.setText(book?.actionPlan)
            book_image.setImageBitmap(
                BitmapFactory.decodeByteArray(
                    book?.image,
                    0,
                    book?.image!!.size
                )
            )
        }

        //firebaseのdatabaseに登録された時のリスナーを設定
        val dataStore = FirebaseDatabase.getInstance()
        val reference = dataStore.getReference("books")
        reference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                //現在保存されているデータの数を取得する。
                firebaseMaxId = snapshot.childrenCount
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@RegisterActivity, "データ登録失敗", Toast.LENGTH_LONG).show()
            }
        })


        save_button.setOnClickListener {
            if (id == -1) {
                if (book_title_input.text.toString() == "" || book_date_input.text.toString() == "" ||
                    book_notice_input.text.toString() == "" || book_actionPlan_input.text.toString() == ""
                ) {
                    Toast.makeText(this, "未入力の項目があります。", Toast.LENGTH_LONG).show()
                } else {

                    val title = book_title_input.text.toString()
                    val date = book_date_input.text.toString()
                    val notice = book_notice_input.text.toString()
                    val actionPlan = book_actionPlan_input.text.toString()
                    val byteArrayOutputStream = ByteArrayOutputStream()
                    (book_image.drawable as BitmapDrawable).bitmap.compress(
                        Bitmap.CompressFormat.PNG,
                        100,
                        byteArrayOutputStream
                    )

                    bookListRealm.executeTransaction {
                        val maxId = bookListRealm.where<BookListObject>().max("id")
                        val nextId = (maxId?.toInt() ?: 0) + 1
                        val book = bookListRealm.createObject<BookListObject>(nextId)
                        book.title = title
                        book.date = date
                        book.notice = notice
                        book.actionPlan = actionPlan
                        book.image = byteArrayOutputStream.toByteArray()
                    }

                    graphRealm.executeTransaction {
                        var graph = graphRealm.where<GraphObject>().findFirst()
                        val calendar = getInstance()
                        val year = calendar.get(YEAR)
                        val month = calendar.get(MONTH) + 1

                        if (graph == null) {
                            graph = graphRealm.createObject()
                            graph.graphList.add(GraphYearObject())
                            graph.graphList[0]?.year = year
                        }

                        var graphYear = graph.graphList[0]


                        if (graphYear?.year != year) {
                            //年を決める
                            for(i in 0..graph.graphList.size - 1) {
                                if (year == graph.graphList[i]?.year) {
                                    graphYear = graph.graphList[i]
                                    break
                                }

                                if (i == graph.graphList.size - 1) {
                                    graph.graphList.add(GraphYearObject())
                                    graphYear = graph.graphList[i + 1]
                                    graphYear?.year = year
                                }

                            }
                        }
                        when (month) {
                            1 -> {
                                graphYear?.jan = graphYear?.jan?.plus(1F)!!
                            }

                            2 -> {
                                graphYear?.feb = graphYear?.feb?.plus(1F)!!
                            }

                            3 -> {
                                graphYear?.mar = graphYear?.mar?.plus(1F)!!
                            }

                            4 -> {
                                graphYear?.apr = graphYear?.apr?.plus(1F)!!
                            }

                            5 -> {
                                graphYear?.may = graphYear?.may?.plus(1F)!!
                            }

                            6 -> {
                                graphYear?.jun = graphYear?.jun?.plus(1F)!!
                            }

                            7 -> {
                                graphYear?.jul = graphYear?.jul?.plus(1F)!!
                            }

                            8 -> {
                                graphYear?.aug = graphYear?.aug?.plus(1F)!!
                            }

                            9 -> {
                                graphYear?.sep = graphYear?.sep?.plus(1F)!!
                            }

                            10 -> {
                                graphYear?.oct = graphYear?.oct?.plus(1F)!!
                            }

                            11 -> {
                                graphYear?.nov = graphYear?.nov?.plus(1F)!!
                            }

                            12 -> {
                                graphYear?.dec = graphYear?.dec?.plus(1F)!!
                            }
                        }

                        //Firebaseに登録する。
                        val bookData = BookHelper(title, date, notice, actionPlan, byteArrayOutputStream.toString())

                        reference.child((firebaseMaxId + 1).toString()).setValue(bookData)

                        Toast.makeText(this, "Firebaseに登録成功", Toast.LENGTH_LONG).show()
                    }




                    AlertDialog.Builder(this)
                        .setMessage("追加しました")
                        .setPositiveButton("OK") { dialog, which ->
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                        }
                        .show()
                }

            } else {

                bookListRealm.executeTransaction {
                    val book = bookListRealm.where<BookListObject>().equalTo("id", id).findFirst()
                    book?.title = book_title_input.text.toString()
                    book?.date = book_date_input.text.toString()
                    book?.notice = book_notice_input.text.toString()
                    book?.actionPlan = book_actionPlan_input.text.toString()

                    val byteArrayOutputStream = ByteArrayOutputStream()
                    (book_image.drawable as BitmapDrawable).bitmap.compress(
                        Bitmap.CompressFormat.PNG,
                        100,
                        byteArrayOutputStream
                    )

                    book?.image = byteArrayOutputStream.toByteArray()
                }
                AlertDialog.Builder(this)
                    .setMessage("変更しました")
                    .setPositiveButton("OK") { dialog, which ->
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }
                    .show()
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

        barcode_button.setOnClickListener {
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
            val resultBarcode =
                IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

            if (resultBarcode != null) {
                //nullだった場合。トーストの表示
                if (resultBarcode.contents == null) {
                    Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
                } else {
                    //バーコードスキャナーで受け取った値を代入する。ボタンのリスナー処理に戻る
                    isbn = resultBarcode.contents

                    //無事に取得できればGoogleBooksAPIに接続し本の画像とタイトルを取得

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

                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        }
                    })

                }
            } else {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    //バーコードリーダー起動メソッド
    private fun takeBarCode() {
        Toast.makeText(this, "上のバーコードを撮ってください。", Toast.LENGTH_LONG).show()
        val intentIntegrator = IntentIntegrator(this)
            .setBeepEnabled(false)
            .apply {
                captureActivity = PortraitActivity::class.java
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
            if (imageUrl != null) {
                val imageDownload = ImageDownload()
                imageDownload.execute()
            } else {
                editText.setText(title)
            }
        }
    }

    class ImageDownload : AsyncTask<String, String, Bitmap>() {

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

        //ここでUIの更新を行う。ここでは重い処理は行わない。
        override fun onPostExecute(result: Bitmap?) {
            imageView.setImageBitmap(result)
            editText.setText(title)
        }
    }

    //戻るボタンを押したときの処理
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        bookListRealm.close()
        graphRealm.close()
    }
}
