package android.wings.websarva.dokusyokannrijavatokotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.EditText
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
        val editDate = findViewById<EditText>(R.id.action_date_edit)
        editDate.setText(GetDateObject.getToday())

        //日付の入力不可
        editDate.isEnabled = false

        save_action_button.setOnClickListener {
            realm = Realm.getInstance(RealmConfigObject.bookListConfig)

            //テーブルに直接入れる？
            val bookActionPlanObject = ActionPlanObject()
            bookActionPlanObject.date = action_date_edit.text.toString()
            bookActionPlanObject.actionPlans = action_do_edit.text.toString()
            bookActionPlanObject.nextActionPlans = action_next_edit.text.toString()

            //ここからrealmに入れていく
            realm.beginTransaction()
            val book = realm.where<BookListObject>().equalTo("id", id).findFirst()
            book?.actionPlanDairy?.add(bookActionPlanObject)
            realm.commitTransaction()

            finish()
        }
    }

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
        realm.close()
    }
}