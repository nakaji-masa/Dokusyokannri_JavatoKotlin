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
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import android.wings.websarva.dokusyokannrijavatokotlin.views.PortraitActivity
import android.wings.websarva.dokusyokannrijavatokotlin.R
import android.wings.websarva.dokusyokannrijavatokotlin.realm.`object`.ActionPlanObject
import android.wings.websarva.dokusyokannrijavatokotlin.realm.`object`.BookObject
import android.wings.websarva.dokusyokannrijavatokotlin.realm.`object`.GraphMonthObject
import android.wings.websarva.dokusyokannrijavatokotlin.realm.`object`.GraphObject
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.model.BookHelper
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.AuthHelper
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.FireStoreHelper
import android.wings.websarva.dokusyokannrijavatokotlin.main.navigator.MainNavigator
import android.wings.websarva.dokusyokannrijavatokotlin.realm.manager.RealmManager
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
import kotlinx.android.synthetic.main.activity_register.*
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*
import java.util.Calendar.*

class RegisterActivity : AppCompatActivity(), TextWatcher {

    private lateinit var bookRealm: Realm
    private lateinit var graphRealm: Realm
    private var bookObj: BookObject? = null
    private var saveMenu: Button? = null
    private var imagePath = GlideHelper.defaultImageUrl

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // バーの設定
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = getString(R.string.register_title)

        // realm
        bookRealm = RealmManager.getBookRealmInstance()
        graphRealm = RealmManager.getGraphRealmInstance()

        // EditTextを監視
        registerBookTitleInput.addTextChangedListener(this)
        registerBookAuthorInput.addTextChangedListener(this)
        registerBookActionPlanInput.addTextChangedListener(this)

        // 別アクティビティーから値を取得
        intent.getStringExtra(INTENT_BOOK_OBJECT_ID)?.let {
            bookObj = bookRealm.where(BookObject::class.java).equalTo("id", it).findAll().last()
        }

        bookObj?.let {
            // 更新なので入力欄を埋める
            title = getString(R.string.update_title)
            registerBookTitleInput.setText(it.title)
            registerBookAuthorInput.setText(it.author)
            registerBookActionPlanInput.setText(it.actionPlan)
            GlideHelper.viewBookImage(it.imageUrl, registerBookImageInput)
            imagePath = it.imageUrl

            // アクションプランが2以上登録されていれば、アクションプランを入力しないようにする
            if (it.actionPlanDairy.size != 1) {
                registerBookActionPlan.visibility = View.INVISIBLE
                registerBookActionPlanInput.visibility = View.INVISIBLE
            }
        }

        registerBookImageInput.setOnClickListener {
           /*
            *ユーザーの端末にカメラ機能があるかどうかの確認
            * Intentに入っている機能を使いたい。(MediaStore.ACTION_IMAGE_CAPTURE)
            * resolveActivityで確認する
            **/
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.book_register_menu, menu)

        // 保存ボタンの設定
        saveMenu = menu?.findItem(R.id.saveItem)?.actionView?.findViewById(R.id.saveButton)
        saveMenu?.apply {
            if (bookObj == null) {
                text = getString(R.string.save_button_text)
                setOnClickListener {
                    saveBookData()
                }
            } else {
                text = getString(R.string.update_button_text)
                isEnabled = true
                setOnClickListener {
                    update()
                }
            }
        }

        // バーコードメニューの設定
        menu?.findItem(R.id.barcodeItem)?.actionView?.findViewById<ImageView>(R.id.barcodeImage)
            ?.setOnClickListener {
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
        isAllInput()
    }

    override fun afterTextChanged(s: Editable?) {
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //バーコードから来た場合
        val resultBarcode =
            IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

        if (resultBarcode != null) {
            //nullだった場合。トーストの表示
            if (resultBarcode.contents != null) {
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
                        // 失敗したときのログを出力
                        e.printStackTrace()

                        // トーストの表示
                        Toast.makeText(this@RegisterActivity, "情報取得に失敗しました", Toast.LENGTH_LONG)
                            .show()
                    }

                    override fun onResponse(call: Call, response: Response) {
                        //成功したときの命令
                        try {
                            //jsonデータの全体取得
                            val rootJson = JSONObject(response.body?.string()!!)

                            //"items"タグのデータを取得。配列でないといけない。
                            val items = rootJson.getJSONArray(JSON_ITEMS)

                            //取得したitemを取得する
                            for (i in 0 until items.length()) {
                                val item = items.getJSONObject(i)

                                //volumeInfoを取得する
                                val volumeInfo = item.getJSONObject(JSON_VOLUME_INFO)

                                //タイトルを取得する
                                val title = volumeInfo.getString(JSON_TITLE)

                                //著者を取得する
                                val authors = volumeInfo.getString(JSON_AUTHORS)

                                // 画像を取得する
                                val imageLinks = volumeInfo.getJSONObject(JSON_IMAGE_LINKS)
                                val thumbnail = imageLinks.getString(JSON_THUMBNAIL)

                                // UI実行
                                val runnable = Runnable {
                                    registerBookTitleInput.setText(title)
                                    registerBookAuthorInput.setText(authors.substring(2..authors.length - 3))
                                    Glide.with(this@RegisterActivity).load(thumbnail)
                                        .into(registerBookImageInput)
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

    /**
     * バーコードスキャンを起動するメソッド
     */
    private fun takeBarCode() {
        Toast.makeText(this, "上のバーコードを撮ってください。", Toast.LENGTH_LONG).show()
        IntentIntegrator(this)
            .setBeepEnabled(false)
            .apply {
                captureActivity = PortraitActivity::class.java
            }.initiateScan()
    }

    /**
     * カメラの権限を得ているか判定するメソッド
     * @return Boolean
     */
    private fun checkCameraPermission() = PackageManager.PERMISSION_GRANTED ==
            ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.CAMERA)
            && PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(
        applicationContext,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    /**
     * ユーザーに権限を要求するメソッド
     */
    private fun grantBarCodePermission() =
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            BARCODE_PERMISSION_REQUEST_CODE
        )


    /**
     * 本のデータを保存するメソッド
     */
    private fun saveBookData() {

        val bookId = UUID.randomUUID().toString()
        val title = registerBookTitleInput.text.toString()
        val date = DateHelper.getToday()
        val author = registerBookAuthorInput.text.toString()
        val actionPlan = registerBookActionPlanInput.text.toString()

        bookRealm.executeTransaction {
            // realmに本の情報を登録
            val book = it.createObject<BookObject>(bookId)
            book.title = title
            book.date = date
            book.author = author
            book.actionPlan = actionPlan
            book.imageUrl = imagePath
            book.uid = AuthHelper.getUid()

            // アクション関係の情報を登録
            val actionObj = it.createObject<ActionPlanObject>(UUID.randomUUID().toString())
            actionObj.title = getString(R.string.action_first)
            actionObj.nextAction = actionPlan
            book.actionPlanDairy.add(actionObj)
        }

        graphRealm.executeTransaction {
            // 現在の日付を取得
            val calendar = getInstance()
            val year = calendar.get(YEAR)
            val month = calendar.get(MONTH)
            val record = graphRealm.where(GraphObject::class.java).findAll()

            // 現在の年のデータが登録されていればtrue
            if (record.any { it.year == year }) {
                // 現在の年のレコードを取得
                val thisYearGraphObj = record.last { it.year == year }

                // 現在の月にカウント
                thisYearGraphObj.graphList[month]?.let {
                    it.readCount += 1
                }

            } else {

                // 新しくオブジェクトを作成する。（年）
                val graph = graphRealm.createObject(GraphObject::class.java, year)
                for (i in 0..11) {
                    // 新しくオブジェクトを作成する（1月~12月分）
                    val graphMonthObj = graphRealm.createObject(GraphMonthObject::class.java)
                    graph.graphList.add(i, graphMonthObj)
                }
                // 現在の月にカウントする
                graph.graphList[month]?.let {
                    it.readCount += 1
                }

            }
        }

        // fragment再生成のフラグを立てる
        MainNavigator.setBookFlag()

        // ダイアログの表示
        AlertDialog.Builder(this)
            .setMessage(getString(R.string.dialog_register_message))
            .setPositiveButton(getString(R.string.dialog_positive)) { dialog, _ ->
                dialog.dismiss()
                finish()
            }
            .show()

        // fireStoreに登録
        FireStoreHelper.savePostData(
            BookHelper(
                docId = bookId,
                title = title,
                author = author,
                action = actionPlan,
                imageUrl = imagePath
            )
        )
    }

    /**
     * 本の情報を更新するメソッド
     */
    private fun update() {
        bookRealm.executeTransaction {
            bookObj?.title = registerBookTitleInput.text.toString()
            bookObj?.author = registerBookAuthorInput.text.toString()
            bookObj?.imageUrl = imagePath
        }
        AlertDialog.Builder(this)
            .setMessage(getString(R.string.dialog_update_message))
            .setPositiveButton(getString(R.string.dialog_positive)) { dialog, _ ->
                dialog.dismiss()
                finish()
            }
            .show()
    }

    /**
     * カメラの権限を得ていればバーコードスキャン、得ていなければ権限許可を要求するメソッド
     */
    private fun searchBook() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).resolveActivity(packageManager)?.let {
            if (checkCameraPermission()) {
                takeBarCode()

            } else {
                grantBarCodePermission()
            }

        } ?: Toast.makeText(this, "カメラを扱うアプリがありません", Toast.LENGTH_LONG).show()
    }


    /**
     * 本のタイトルとアクションプランが入力されていれば、保存ボタンを活性化するメソッド
     */
    private fun isAllInput() {
        saveMenu?.isEnabled =
            !(registerBookTitleInput.text.isNullOrBlank() || registerBookActionPlanInput.text.isNullOrBlank()
                    || registerBookAuthorInput.text.isNullOrBlank())
    }

    companion object {
        const val BARCODE_PERMISSION_REQUEST_CODE = 1
        const val INTENT_BOOK_OBJECT_ID = "book_object"
        const val JSON_ITEMS = "items"
        const val JSON_VOLUME_INFO = "volumeInfo"
        const val JSON_TITLE = "title"
        const val JSON_AUTHORS = "authors"
        const val JSON_IMAGE_LINKS = "imageLinks"
        const val JSON_THUMBNAIL = "thumbnail"
    }

}
