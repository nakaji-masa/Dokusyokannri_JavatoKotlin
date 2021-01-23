package android.wings.websarva.dokusyokannrijavatokotlin.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import android.wings.websarva.dokusyokannrijavatokotlin.views.PortraitActivity
import android.wings.websarva.dokusyokannrijavatokotlin.R
import android.wings.websarva.dokusyokannrijavatokotlin.realm.`object`.BookObject
import android.wings.websarva.dokusyokannrijavatokotlin.realm.`object`.GraphMonthObject
import android.wings.websarva.dokusyokannrijavatokotlin.realm.`object`.GraphObject
import android.wings.websarva.dokusyokannrijavatokotlin.realm.config.RealmConfigObject
import android.wings.websarva.dokusyokannrijavatokotlin.register.BookHelper
import android.wings.websarva.dokusyokannrijavatokotlin.utils.AuthHelper
import android.wings.websarva.dokusyokannrijavatokotlin.utils.FireStoreHelper
import android.wings.websarva.dokusyokannrijavatokotlin.utils.DateHelper
import android.wings.websarva.dokusyokannrijavatokotlin.utils.GlideHelper
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.zxing.integration.android.IntentIntegrator
import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_register.*
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*
import java.util.Calendar.*

class RegisterActivity : AppCompatActivity(), TextWatcher {

    private lateinit var bookListRealm: Realm
    private lateinit var graphRealm: Realm
    private  var menuSaveButton: Button? = null
    private var id: String? = null
    private var imagePath = R.drawable.ic_book_default_image.toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        bookListRealm = Realm.getInstance(RealmConfigObject.bookListConfig)
        graphRealm = Realm.getInstance(RealmConfigObject.graphConfig)

        registerBookTitleInput.addTextChangedListener(this)
        registerBookActionPlanInput.addTextChangedListener(this)

        //日付の入力不可
        registerBookDateInput.setText(DateHelper.getToday())
        registerBookDateInput.isEnabled = false

        id = intent.getStringExtra(INTENT_ID)

        if (id != null) {
            val book = bookListRealm.where<BookObject>().equalTo("id", id).findFirst()
            registerBookTitleInput.setText(book?.title)
            registerBookDateInput.setText(book?.date)
            registerBookActionPlanInput.setText(book?.actionPlan)
            book?.imageUrl?.let {
                GlideHelper.viewGlide(it, registerBookImageInput)
                imagePath = it
            }
        }

        registerBookImageInput.setOnClickListener {
            //ユーザーの端末にカメラ機能があるかどうかの確認
            //Intentに入っている機能を使いたい。(MediaStore.ACTION_IMAGE_CAPTURE)
            //resolveActivityで確認する
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).resolveActivity(packageManager)?.let {
                //nullが返されなければ、カメラを起動かパーミッションチェックを行う。
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
        //バーコードから来た場合
        val resultBarcode =
            IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

        if (resultBarcode != null) {
            //nullだった場合。トーストの表示
            if (resultBarcode.contents == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
            } else {
                //バーコードスキャナーで受け取った値を代入する。ボタンのリスナー処理に戻る
                val isbn = resultBarcode.contents

                //無事に取得できればGoogleBooksAPIに接続し本の画像とタイトルを取得
                val url = "https://www.googleapis.com/books/v1/volumes?q=isbn:$isbn"

                //handlerを宣言
                val handler = Handler()

                //okHttpクライアントの設定
                val okHttpClient = OkHttpClient()

                //リクエストの中に情報を入れ込む
                val request = Request.Builder().url(url).build()

                //Callバックの中に情報を入れ込む。そして、非同期で実装。
                okHttpClient.newCall(request).enqueue(object : Callback {

                    override fun onFailure(call: Call, e: IOException) {
                        //失敗したときのログを出力
                       e.printStackTrace()
                    }

                    override fun onResponse(call: Call, response: Response) {
                        //成功したときの命令
                        try {
                            //jsonデータの全体取得
                            val rootJson = JSONObject(response.body?.string())

                            //"items"タグのデータを取得。配列でないといけない。
                            val items = rootJson.getJSONArray(JSON_ITEMS)
                            println(items)

                            //取得したitemを取得する
                            for (i in 0 until items.length()) {
                                val item = items.getJSONObject(i)

                                //volumeInfoを取得する
                                val volumeInfo = item.getJSONObject(JSON_VOLUME_INFO)

                                //タイトルを取得する
                                title = volumeInfo.getString(JSON_TITLE)

                                //著者を取得する
                                val authors = volumeInfo.getString(JSON_AUTHORS)

                                //imageLinksを取得する
                                val imageLinks = volumeInfo.getJSONObject(JSON_IMAGE_LINKS)

                                val thumbnail = imageLinks.getString(JSON_THUMBNAIL)

                                val runnable = Runnable {
                                    registerBookTitleInput.setText(title)
                                    registerBookAuthorInput.setText(authors)
                                    Glide.with(this@RegisterActivity).load(thumbnail).into(registerBookImageInput)
                                    imagePath = thumbnail
                                }

                                handler.post(runnable)
                            }
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

    //バーコードリーダー起動メソッド
    private fun takeBarCode() {
        Toast.makeText(this, "上のバーコードを撮ってください。", Toast.LENGTH_LONG).show()
        IntentIntegrator(this)
            .setBeepEnabled(false)
            .apply {
                captureActivity = PortraitActivity::class.java
            }.initiateScan()
    }

    //カメラ機能とストレージ機能のパーミッションをとっているか確認。
    //持っていればtrueを返す。
    private fun checkCameraPermission() = PackageManager.PERMISSION_GRANTED ==
            ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.CAMERA)
            && PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(
        applicationContext,
        Manifest.permission.READ_EXTERNAL_STORAGE
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
        //バーコードリクエストコードを持っていたらバーコードを起動する。
        if (requestCode == BARCODE_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                takeBarCode()
            }
        }
    }

    private fun saveBookData(id: String?) {
        if (id == null) {
            val title = registerBookTitleInput.text.toString()
            val date = registerBookDateInput.text.toString()
            val author = registerBookAuthorInput.text.toString()
            val actionPlan = registerBookActionPlanInput.text.toString()

            bookListRealm.executeTransaction {
                val maxId = bookListRealm.where<BookObject>().max("id")
                val nextId = (maxId?.toInt() ?: 0) + 1
                val book = bookListRealm.createObject<BookObject>(nextId)
                //ドキュメントのidを取得。realmと連動した形にする。
                book.title = title
                book.date = date
                book.actionPlan = actionPlan
                book.imageUrl = imagePath
            }

            graphRealm.executeTransaction {
                val calendar = getInstance()
                val year = calendar.get(YEAR)
                val month = calendar.get(MONTH)
                val record = graphRealm.where(GraphObject::class.java).findAll()

                if (!record.any { it.year == year }) {
                    val graph = graphRealm.createObject(GraphObject::class.java, year)
                    for (i in 0..11) {
                        val graphMonthObj = graphRealm.createObject(GraphMonthObject::class.java)
                        graph.graphList.add(i, graphMonthObj)
                    }
                    graph.graphList[month]?.let {
                        it.readCount += 1
                    }
                } else {
                    val thisYearGraphObj = record.last { it.year == year }
                    thisYearGraphObj.graphList[month]?.let {
                        it.readCount += 1
                    }
                }
            }

            val docId = UUID.randomUUID().toString()


            val book = BookHelper(
                docId,
                DateHelper.getToday(),
                title,
                actionPlan,
                author,
                imagePath,
                AuthHelper.getUid(),
                mutableListOf(),
                mutableListOf(),
                Date()
            )

            FireStoreHelper.savePostData(book)

            AlertDialog.Builder(this)
                .setMessage("追加しました")
                .setPositiveButton("OK") { dialog, which ->
                    finish()
                }
                .show()


        } else {
            bookListRealm.executeTransaction {
                val book = bookListRealm.where<BookObject>().equalTo("id", id).findFirst()
                book?.title = registerBookTitleInput.text.toString()
                book?.date = registerBookDateInput.text.toString()
                book?.actionPlan = registerBookActionPlanInput.text.toString()
                book?.imageUrl = imagePath
            }
            AlertDialog.Builder(this)
                .setMessage("変更しました")
                .setPositiveButton("OK") { dialog, which ->
                    finish()
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
        menuInflater.inflate(R.menu.book_register_menu, menu)
        val saveItem = menu?.findItem(R.id.saveItem)
        val barcodeItem = menu?.findItem(R.id.barcodeItem)
        menuSaveButton = saveItem?.actionView?.findViewById(R.id.saveButton)
        val menuBarcodeImage = barcodeItem?.actionView?.findViewById<ImageView>(R.id.barcodeImage)
        menuSaveButton?.setOnClickListener {
            saveBookData(id)
        }
        menuBarcodeImage?.setOnClickListener {
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
        menuSaveButton?.isEnabled =
            !(registerBookTitleInput.text.isNullOrBlank() || registerBookActionPlanInput.text.isNullOrBlank())
    }

    override fun onDestroy() {
        super.onDestroy()
        bookListRealm.close()
        graphRealm.close()
    }

    companion object {
        const val BARCODE_PERMISSION_REQUEST_CODE = 1
        const val INTENT_ID = "id"
        const val JSON_ITEMS = "items"
        const val JSON_VOLUME_INFO = "volumeInfo"
        const val JSON_TITLE = "title"
        const val JSON_AUTHORS = "authors"
        const val JSON_IMAGE_LINKS = "imageLinks"
        const val JSON_THUMBNAIL = "thumbnail"
    }

}
