package android.wings.websarva.dokusyokannrijavatokotlin.another

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.wings.websarva.dokusyokannrijavatokotlin.R
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.model.BookHelper
import android.wings.websarva.dokusyokannrijavatokotlin.utils.GlideHelper
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions


class AnotherUserContentBookShelfAdapter(options: FirestoreRecyclerOptions<BookHelper>): FirestoreRecyclerAdapter<BookHelper, AnotherUserContentBookShelfAdapter.ViewHolder>(options) {

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val bookImage: ImageView = view.findViewById(R.id.cellBookImage)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.bookshelf_cell, parent, false)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: BookHelper) {
       GlideHelper.viewBookImage(model.imageUrl, holder.bookImage)
    }
}