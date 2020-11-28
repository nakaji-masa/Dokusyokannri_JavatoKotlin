package android.wings.websarva.dokusyokannrijavatokotlin.booklist

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.wings.websarva.dokusyokannrijavatokotlin.R
import android.wings.websarva.dokusyokannrijavatokotlin.realm.`object`.BookListObject
import android.wings.websarva.dokusyokannrijavatokotlin.utils.GlideHelper
import androidx.recyclerview.widget.RecyclerView
import io.realm.OrderedRealmCollection
import io.realm.RealmRecyclerViewAdapter

class BookListAdapter(
    private val context: Context,
    private var bookList: OrderedRealmCollection<BookListObject>,
    private val autoUpdate: Boolean
) : RealmRecyclerViewAdapter<BookListObject, BookListAdapter.BookViewHolder>(bookList, autoUpdate) {

    lateinit var listener: OnItemClickListener

    class BookViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val bookImage: ImageView = view.findViewById(R.id.bookListCellImage)
        val bookTitle: TextView = view.findViewById(R.id.bookListCellTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.recycle_cell_main, parent, false)
        return BookViewHolder(v)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book: BookListObject = bookList.get(position) ?: return

        GlideHelper.viewGlide(book.image, holder.bookImage)

        holder.bookTitle.text = book.title

        holder.itemView.setOnClickListener {
            listener.onItemClickListener(it, position, book.id)
        }

    }

    override fun getItemCount(): Int {
        return bookList.size
    }

    interface OnItemClickListener {
        fun onItemClickListener(view: View, position: Int, clickedId: Int?)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }


}