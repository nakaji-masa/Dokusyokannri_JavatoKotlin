package android.wings.websarva.dokusyokannrijavatokotlin.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.EditText
import android.wings.websarva.dokusyokannrijavatokotlin.utils.DateHelper
import android.wings.websarva.dokusyokannrijavatokotlin.R
import android.wings.websarva.dokusyokannrijavatokotlin.realm.`object`.ActionPlanObject
import android.wings.websarva.dokusyokannrijavatokotlin.realm.`object`.BookListObject
import android.wings.websarva.dokusyokannrijavatokotlin.realm.config.RealmConfigObject
import io.realm.Realm
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_action_register.*

class ActionRegisterActivity : AppCompatActivity() {

    private var id: Int? = null
    private lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_action_register)

        id = intent.getIntExtra("id", 0)

        //日付の取得
        val editDate = findViewById<EditText>(R.id.actionRegisterDateInput)
        editDate.setText(DateHelper.getToday())

        //日付の入力不可
        editDate.isEnabled = false

        actionRegisterSaveButton.setOnClickListener {
            realm = Realm.getInstance(RealmConfigObject.bookListConfig)

            //テーブルに直接入れる？
            val bookActionPlanObject = ActionPlanObject()
            bookActionPlanObject.date = actionRegisterDateInput.text.toString()
            bookActionPlanObject.actionPlans = actionRegisterDoInput.text.toString()
            bookActionPlanObject.nextActionPlans = actionRegisterDoNextInput.text.toString()

            //ここからrealmに入れていく
            realm.beginTransaction()
            val book = realm.where<BookListObject>().equalTo("id", id).findFirst()
            book?.actionPlanDairy?.add(bookActionPlanObject)
            realm.commitTransaction()

            finish()
        }
    }

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

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
}