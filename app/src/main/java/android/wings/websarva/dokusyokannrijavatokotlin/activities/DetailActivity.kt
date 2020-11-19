package android.wings.websarva.dokusyokannrijavatokotlin.activities

import android.os.Bundle
import android.view.MenuItem
import android.wings.websarva.dokusyokannrijavatokotlin.detail.DetailTabAdapter
import android.wings.websarva.dokusyokannrijavatokotlin.R
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val id = intent.getIntExtra("id", 0)

        //アダプターのセット
        detail_pager.adapter = DetailTabAdapter(supportFragmentManager, this, id)

        //タブにpagerの情報をセット
        detail_tabLayout.setupWithViewPager(detail_pager)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        } else {
            return super.onOptionsItemSelected(item)
        }

    }

}
