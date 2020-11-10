package android.wings.websarva.dokusyokannrijavatokotlin

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.realm.OrderedRealmCollection
import io.realm.RealmRecyclerViewAdapter

class BookListAdapter(
    private val context: Context,
    private var bookList: OrderedRealmCollection<BookListObject>,
    private val autoUpdate: Boolean
) : RealmRecyclerViewAdapter<BookListObject, BookListAdapter.BookViewHolder>(bookList, autoUpdate) {

    lateinit var listener: OnItemClickListener

    class BookViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        val bookImage : ImageView = view.findViewById(R.id.book_recycle_image)
        val bookTitle : TextView = view.findViewById(R.id.book_recycle_title)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.recycle_cell_main, parent, false)
        return BookViewHolder(v)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book: BookListObject = bookList.get(position) ?: return

        holder.bookImage.setImageBitmap(book.image?.size?.let {
            BitmapFactory.decodeByteArray(book.image, 0, it)
        })

        holder.bookTitle.text = book.title

        holder.itemView.setOnClickListener {
            listener.onItemClickListener(it, position, book.id)
        }

    }

    override fun getItemCount(): Int {
        return bookList.size
    }

    interface OnItemClickListener{
        fun onItemClickListener(view: View, position: Int, clickedId: Int?)
    }

    fun setOnItemClickListener(listener: OnItemClickListener){
        this.listener = listener
    }


}