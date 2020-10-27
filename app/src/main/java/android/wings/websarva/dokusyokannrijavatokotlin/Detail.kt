package android.wings.websarva.dokusyokannrijavatokotlin

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import io.realm.Realm
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.fragment_detail.*

class Detail : AppCompatActivity() {

    private lateinit var realm: Realm
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        realm = Realm.getDefaultInstance()

        val  id = intent.getIntExtra("id", 0)

        //アダプターのセット
        detail_pager.adapter = DetailTabAdapter(supportFragmentManager, this, id)

        //タブにpegerの情報をセット
        detail_tabLayout.setupWithViewPager(detail_pager)


    }
}
