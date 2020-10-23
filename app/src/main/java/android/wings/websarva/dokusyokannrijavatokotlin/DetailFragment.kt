package android.wings.websarva.dokusyokannrijavatokotlin

import android.database.sqlite.SQLiteDatabase
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.realm.Realm
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_register.view.*
import kotlinx.android.synthetic.main.fragment_detail.*
import kotlinx.android.synthetic.main.fragment_detail.view.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class DetailFragment : Fragment() {
    private var id: Int?  = null
    private lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            id = it.getInt("id")
            println(id!!)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //返すビュー変数を作成
        val view = inflater.inflate(R.layout.fragment_detail, container, false)

        realm = Realm.getDefaultInstance()

        println(id)
        val book = realm.where<BookListObject>().equalTo("id", id).findFirst()

        view.book_title_view.text = book?.title
        view.book_date_view.text = book?.date
        view.book_notice_view.text = book?.notice
        view.book_actionPlan_view.text = book?.actionPlan
        view.book_image_view.setImageBitmap(book?.image?.size?.let {
            BitmapFactory.decodeByteArray(
                book?.image, 0,
                it
            )
        })
        return view
    }

    companion object {

        @JvmStatic
        fun newInstance(param1 : Int) : DetailFragment{
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
}
