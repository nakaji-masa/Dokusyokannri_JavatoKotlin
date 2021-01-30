package android.wings.websarva.dokusyokannrijavatokotlin.activities

import android.os.Bundle
import android.wings.websarva.dokusyokannrijavatokotlin.bookshelf.fragments.BookShelfFragment
import android.wings.websarva.dokusyokannrijavatokotlin.graph.fragments.GraphFragment
import android.wings.websarva.dokusyokannrijavatokotlin.R
import android.wings.websarva.dokusyokannrijavatokotlin.library.fragments.LibraryFragment
import android.wings.websarva.dokusyokannrijavatokotlin.main.fragments.MainSelectFragment
import android.wings.websarva.dokusyokannrijavatokotlin.settings.fragments.SettingsFragment
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.mainContainer, MainSelectFragment.newInstance())
        transaction.commit()
    }
}




