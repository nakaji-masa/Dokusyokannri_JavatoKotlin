package android.wings.websarva.dokusyokannrijavatokotlin.user.content.myshelf

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.wings.websarva.dokusyokannrijavatokotlin.databinding.BookshelfCellBinding
import android.wings.websarva.dokusyokannrijavatokotlin.realm.`object`.BookObject
import android.wings.websarva.dokusyokannrijavatokotlin.utils.GlideHelper
import androidx.recyclerview.widget.RecyclerView
import io.realm.OrderedRealmCollection
import io.realm.RealmRecyclerViewAdapter

class MyBookshelfAdapter(
    private val context: Context,
    private val book: OrderedRealmCollection<BookObject>,
    autoUpdate: Boolean
) : RealmRecyclerViewAdapter<BookObject, MyBookshelfAdapter.BookViewHolder>(book, autoUpdate) {

    lateinit var listener: OnItemClickListener

    inner class BookViewHolder(val itemBinding: BookshelfCellBinding) : RecyclerView.ViewHolder(itemBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val itemBinding = BookshelfCellBinding.inflate(LayoutInflater.from(context), parent, false)
        return BookViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book: BookObject = book[position] ?: return

        GlideHelper.viewBookImage(book.imageUrl, holder.itemBinding.cellBookImage)

        holder.itemBinding.root.setOnClickListener {
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