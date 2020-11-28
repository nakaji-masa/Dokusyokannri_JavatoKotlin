package android.wings.websarva.dokusyokannrijavatokotlin.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import android.wings.websarva.dokusyokannrijavatokotlin.utils.GetDateHelper
import android.wings.websarva.dokusyokannrijavatokotlin.views.PortraitActivity
import android.wings.websarva.dokusyokannrijavatokotlin.R
import android.wings.websarva.dokusyokannrijavatokotlin.realm.`object`.BookListObject
import android.wings.websarva.dokusyokannrijavatokotlin.realm.`object`.GraphObject
import android.wings.websarva.dokusyokannrijavatokotlin.realm.`object`.GraphYearObject
import android.wings.websarva.dokusyokannrijavatokotlin.realm.config.RealmConfigObject
import android.wings.websarva.dokusyokannrijavatokotlin.register.BookHelper
import android.wings.websarva.dokusyokannrijavatokotlin.utils.GlideHelper
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.zxing.integration.android.IntentIntegrator
import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_register.*
import okhttp3.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar.*

@Suppress(
    "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS",
    "RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS"
)
class RegisterActivity : AppCompatActivity(), TextWatcher {

    companion object {
        const val CAMERA_REQUEST_CODE = 1
        const val CAMERA_PERMISSION_REQUEST_CODE = 2
        const val BARCODE_PERMISSION_REQUEST_CODE = 3
        var id: Int = -1
        var isbn: String = ""
        var title: String = ""
        var imageUrl: String = ""
        var imageUri: Uri? = null
        lateinit var menuSaveButton: Button
        lateinit var menuBarcodeImage: ImageView
        lateinit var handler: Handler
        lateinit var imageView: ImageView
        lateinit var editText: EditText
    }

    private lateinit var bookListRealm: Realm
    private lateinit var graphRealm: Realm
    private var documentId: Long = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        setTitle(R.string.registerButton)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        bookListRealm = Realm.getInstance(RealmConfigObject.bookListConfig)
        graphRealm = Realm.getInstance(RealmConfigObject.graphConfig)

        handler = Handler()
        imageView = findViewById(R.id.registerBookImageInput)
        editText = findViewById(R.id.registerBookTitleInput)

        registerBookTitleInput.addTextChangedListener(this)
        registerBookNoticeInput.addTextChangedListener(this)
        registerBookActionPlanInput.addTextChangedListener(this)

        //日付を取得
        val dateEditText = findViewById<EditText>(R.id.registerBookDateInput)
        dateEditText.setText(GetDateHelper.getToday())

        //日付の入力不可
        dateEditText.isEnabled = false

        id = intent.getIntExtra("id", -1)

        if (id != -1) {
            val book = bookListRealm.where<BookListObject>().equalTo("id", id).findFirst()
            registerBookTitleInput.setText(book?.title)
            registerBookDateInput.setText(book?.date)
            registerBookNoticeInput.setText(book?.notice)
            registerBookActionPlanInput.setText(book?.actionPlan)
            imageUrl = book?.image!!
            GlideHelper.viewGlide(book.image, registerBookImageInput)
            registerBookImageInput.tag = getString(R.string.noDefaultImage)
        }

        registerBookImageInput.setOnClickListener {
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
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            //カメラを使った場合の処理
            registerBookImageInput.setImageURI(imageUri)
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
        IntentIntegrator(this)
            .setBeepEnabled(false)
            .apply {
                captureActivity = PortraitActivity::class.java
            }.initiateScan()
    }

    //カメラアプリの起動メソッド
    @SuppressLint("SimpleDateFormat")
    private fun takePicture() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            addCategory(Intent.CATEGORY_DEFAULT)
        }

        //uriを作成する
        val dateFormat = SimpleDateFormat("yyyyMMdd")
        val date = Date()
        val nowStr = dateFormat.format(date)

        val fileName = "BookActionsPhoto$nowStr.jpg"
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, fileName)
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")

        val resolver = contentResolver

        imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        imageUrl = imageUri.toString()

        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)

        startActivityForResult(intent, CAMERA_REQUEST_CODE)
    }

    //カメラ機能とストレージ機能のパーミッションをとっているか確認。
    //持っていればtrueを返す。
    private fun checkCameraPermission() = PackageManager.PERMISSION_GRANTED ==
            ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.CAMERA)
            && PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(
        applicationContext,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )
    // Manifest.permission.CAMERAっていう許可とっているかな？

    //パーミッションを得るための関数
    private fun grantCameraPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE),
            CAMERA_PERMISSION_REQUEST_CODE
        )

    }

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
                && grantResults[1] == PackageManager.PERMISSION_GRANTED
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

    private class ReflectResult(items: JSONArray) : Runnable {
        init {
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
            if (imageUrl != "") {
                editText.setText(title)
                Glide.with(imageView.context).load(imageUrl).into(imageView)
                imageView.tag = "NoDefaultImage"
            } else {
                editText.setText(title)
            }
        }
    }

    private fun saveBookData() {
        if (id == -1) {
                val title = registerBookTitleInput.text.toString()
                val date = registerBookDateInput.text.toString()
                val notice = registerBookNoticeInput.text.toString()
                val actionPlan = registerBookActionPlanInput.text.toString()

                bookListRealm.executeTransaction {
                    val maxId = bookListRealm.where<BookListObject>().max("id")
                    val nextId = (maxId?.toInt() ?: 0) + 1
                    val book = bookListRealm.createObject<BookListObject>(nextId)
                    //ドキュメントのidを取得。realmと連動した形にする。
                    documentId = nextId.toLong()
                    book.title = title
                    book.date = date
                    book.notice = notice
                    book.actionPlan = actionPlan
                    book.image = imageUrl
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
                        for (i in 0 until graph.graphList.size) {
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
                }

                val db = FirebaseFirestore.getInstance()
                val book = BookHelper(
                    title,
                    date,
                    notice,
                    actionPlan,
                    imageUrl
                )

                ///fireStoreへ登録
                db.collection("books")
                    .document(documentId.toString())
                    .set(book)


                AlertDialog.Builder(this)
                    .setMessage("追加しました")
                    .setPositiveButton("OK") { dialog, which ->
                        finish()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }
                    .show()


        } else {

            bookListRealm.executeTransaction {
                val book = bookListRealm.where<BookListObject>().equalTo("id", id).findFirst()
                book?.title = registerBookTitleInput.text.toString()
                book?.date = registerBookDateInput.text.toString()
                book?.notice = registerBookNoticeInput.text.toString()
                book?.actionPlan = registerBookActionPlanInput.text.toString()
                book?.image = imageUrl
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

    private fun searchBook() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).resolveActivity(packageManager)?.let {
            if (checkCameraPermission()) {
                takeBarCode()

            } else {
                grantBarCodePermission()
            }

        } ?: Toast.makeText(this, "カメラを扱うアプリがありません", Toast.LENGTH_LONG).show()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.register_menu, menu)
        val saveItem  = menu?.findItem(R.id.saveItem)
        val barcodeItem = menu?.findItem(R.id.barcodeItem)
        menuSaveButton = saveItem?.actionView?.findViewById(R.id.saveButton)!!
        menuBarcodeImage = barcodeItem?.actionView?.findViewById(R.id.barcodeImage)!!
        menuSaveButton.setOnClickListener {
            saveBookData()
        }
        menuBarcodeImage.setOnClickListener {
            searchBook()
        }
        return super.onCreateOptionsMenu(menu)
    }

    //戻るボタンを押したときの処理
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        watchAllInput()
    }

    override fun afterTextChanged(s: Editable?) {
    }

    private fun watchAllInput() {
        menuSaveButton.isEnabled = !(registerBookTitleInput.text.isNullOrBlank() || registerBookNoticeInput.text.isNullOrBlank()
                || registerBookActionPlanInput.text.isNullOrBlank())
    }

    override fun onDestroy() {
        super.onDestroy()
        bookListRealm.close()
        graphRealm.close()
    }

}
