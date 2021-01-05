package android.wings.websarva.dokusyokannrijavatokotlin.detail.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.wings.websarva.dokusyokannrijavatokotlin.R
import android.wings.websarva.dokusyokannrijavatokotlin.activities.MainActivity
import android.wings.websarva.dokusyokannrijavatokotlin.activities.RegisterActivity
import android.wings.websarva.dokusyokannrijavatokotlin.realm.`object`.BookListObject
import android.wings.websarva.dokusyokannrijavatokotlin.realm.config.RealmConfigObject
import android.wings.websarva.dokusyokannrijavatokotlin.utils.GlideHelper
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import io.realm.Realm
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.fragment_detail.*
import kotlinx.android.synthetic.main.fragment_detail.view.*

class DetailFragment : Fragment() {
    private var id: Int? = null
    private lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            id = it.getInt("id")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //返すビュー変数を作成
        val view = inflater.inflate(R.layout.fragment_detail, container, false)

        realm = Realm.getInstance(RealmConfigObject.bookListConfig)

        val book = realm.where<BookListObject>().equalTo("id", id).findFirst()

        view.detailBookTitle.text = book?.title
        view.detailBookDate.text = book?.date
        view.detailActionPlan.text = book?.actionPlan
        GlideHelper.viewGlide(book?.image!!, view.detailBookImage)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        //本の内容を削除する
        detailDeleteButton.setOnClickListener {
            realm.executeTransaction {
                realm.where<BookListObject>().equalTo("id", id).findFirst()?.deleteFromRealm()
            }

            AlertDialog.Builder(view.context)
                .setMessage("削除しました。")
                .setPositiveButton("OK") { dialog, which ->
                    val intent = Intent(view.context, MainActivity::class.java)
                    startActivity(intent)
                }.show()
        }

        //本の内容を変更する
        detailChangeButton.setOnClickListener {
            val intent = Intent(view.context, RegisterActivity::class.java)
            intent.putExtra("id", id)
            println(id)
            startActivity(intent)
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: Int): DetailFragment {
            //インスタンスの生成
            val fragment = DetailFragment()

            //値を取得するためのインスタンスを生成
            val bundle = Bundle()

            //値を格納
            bundle.putInt("id", param1)

            //フラグメントにセット
            fragment.arguments = bundle

            //自分だけのフラグメントを返す
            return fragment

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
}
