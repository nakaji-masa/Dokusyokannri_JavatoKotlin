package android.wings.websarva.dokusyokannrijavatokotlin.bookshelf

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.wings.websarva.dokusyokannrijavatokotlin.R
import android.wings.websarva.dokusyokannrijavatokotlin.realm.`object`.BookObject
import android.wings.websarva.dokusyokannrijavatokotlin.utils.GlideHelper
import androidx.recyclerview.widget.RecyclerView
import io.realm.OrderedRealmCollection
import io.realm.RealmRecyclerViewAdapter

class BookshelfAdapter(
    private val context: Context,
    private var book: OrderedRealmCollection<BookObject>,
    autoUpdate: Boolean
) : RealmRecyclerViewAdapter<BookObject, BookshelfAdapter.BookViewHolder>(book, autoUpdate) {

    lateinit var listener: OnItemClickListener

    class BookViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView : ImageView = view.findViewById(R.id.cellBookImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.recycle_cell_main, parent, false)
        return BookViewHolder(v)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book: BookObject = book[position] ?: return

        GlideHelper.viewGlide(book.imageUrl, holder.imageView)

        holder.itemView.setOnClickListener {
            println("adapter:" + book.id)
            listener.onItemClickListener(it, position, book.id)
        }

    }

    override fun getItemCount(): Int {
        return book.size
    }

    interface OnItemClickListener {
        fun onItemClickListener(view: View, position: Int, clickedId: String?)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }


}