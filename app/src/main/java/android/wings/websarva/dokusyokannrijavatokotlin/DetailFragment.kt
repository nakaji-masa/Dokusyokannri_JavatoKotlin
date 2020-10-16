package android.wings.websarva.dokusyokannrijavatokotlin

import android.database.sqlite.SQLiteDatabase
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_detail.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DetailFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DetailFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var id: Int?  = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            id = it.getInt("_id")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //返すビュー変数を作成
        val view = inflater.inflate(R.layout.fragment_detail, container, false)

        val helper = DataBaseHelper(activity)

        val db: SQLiteDatabase = helper.getWritableDatabase()

        try {
            val sql =
                "SELECT bookName, deadline, bookNotice, bookActionPlan, bookImage FROM BookList WHERE _id =$id"
            val cursor = db.rawQuery(sql, null)

            var arrayByte = ByteArray(0)

            while (cursor.moveToNext()) {
                view.book_name_view.text = cursor.getString(cursor.getColumnIndex("bookName"))
                view.book_deadline_view.text = cursor.getString(cursor.getColumnIndex("deadline"))
                view.book_notice_view.text = cursor.getString(cursor.getColumnIndex("bookNotice"))
                view.book_actionplan_view.text = cursor.getString(cursor.getColumnIndex("bookActionPlan"))
                arrayByte = cursor.getBlob(cursor.getColumnIndex("bookImage"))
                view.book_image_view.setImageBitmap(BitmapFactory.decodeByteArray(arrayByte, 0, arrayByte.size))
            }

        } finally {
            db.close()
        }

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DetailFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1 : Int) : DetailFragment{
            //インスタンスの生成
            val fragment = DetailFragment()

            //値を取得するためのインスタンスを生成
            val bundle = Bundle()

            //値を格納
            bundle.putInt("_id", param1)

            //フラグメントにセット
            fragment.arguments = bundle

            //自分だけのフラグメントを返す
            return fragment

        }
    }
}
