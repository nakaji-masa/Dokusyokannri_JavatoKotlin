package android.wings.websarva.dokusyokannrijavatokotlin.activities

import android.os.Bundle
import android.wings.websarva.dokusyokannrijavatokotlin.R
import android.wings.websarva.dokusyokannrijavatokotlin.main.fragments.MainSelectFragment
import android.wings.websarva.dokusyokannrijavatokotlin.realm.manager.RealmManager
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.mainContainer, MainSelectFragment.newInstance())
        transaction.commit()
    }

    override fun onDestroy() {
        RealmManager.close()
        super.onDestroy()
    }
}




