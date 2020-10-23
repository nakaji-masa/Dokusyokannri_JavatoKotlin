package android.wings.websarva.dokusyokannrijavatokotlin

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import kotlinx.android.synthetic.main.activity_detail.*

class Detail : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val  id = intent.getIntExtra("id", 0)

        //アダプターのセット
        detail_pager.adapter = DetailTabAdapter(supportFragmentManager, this, id)

        //タブにpegerの情報をセット
        detail_tabLayout.setupWithViewPager(detail_pager)


    }
}
