package android.wings.websarva.dokusyokannrijavatokotlin

import android.content.Context
import android.database.Cursor
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SimpleCursorAdapter
import android.widget.TextView


@Suppress("DEPRECATION")
class CursorAdapter(
    private val context: Context,
    layout: Int,
    private val c: Cursor,
    from: Array<String?>?,
    to: IntArray?
) :
    SimpleCursorAdapter(context, layout, c, from, to) {

    override fun getView(pos: Int, inView: View?, parent: ViewGroup): View {
        var v: View? = inView

        //画面が出たら
        if (v == null) {
            val inflater = LayoutInflater.from(parent?.context)
            //どんなレイアウトにするか
            v = inflater.inflate(R.layout.booklist_cell, null)
        }


        c.moveToPosition(pos)

        //値を取得
        val id = c.getInt(c.getColumnIndex("_id"))
        val bookname = c.getString(c.getColumnIndex("bookName"))
        val image = c.getBlob(c.getColumnIndex("bookImage"))

        val iv = v?.findViewById<ImageView>(R.id.book_list_Image)
        //bitmap型に変換
        iv?.setImageBitmap(BitmapFactory.decodeByteArray(image, 0, image.size))

        val idstr = id.toString()

        val idText = v?.findViewById<View>(R.id.book_list_id) as TextView
        idText.text = idstr

        val booknameText = v.findViewById<View>(R.id.book_list_name) as TextView
        booknameText.text = bookname

        return v
    }

}