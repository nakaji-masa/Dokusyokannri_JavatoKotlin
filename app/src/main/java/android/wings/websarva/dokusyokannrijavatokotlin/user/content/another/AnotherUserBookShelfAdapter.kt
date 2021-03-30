package android.wings.websarva.dokusyokannrijavatokotlin.user.content.another

import android.view.LayoutInflater
import android.view.ViewGroup
import android.wings.websarva.dokusyokannrijavatokotlin.databinding.BookshelfCellBinding
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.model.BookHelper
import android.wings.websarva.dokusyokannrijavatokotlin.utils.GlideHelper
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions


class AnotherUserBookShelfAdapter(options: FirestoreRecyclerOptions<BookHelper>): FirestoreRecyclerAdapter<BookHelper, AnotherUserBookShelfAdapter.ViewHolder>(options) {

    inner class ViewHolder(val itemBinding: BookshelfCellBinding): RecyclerView.ViewHolder(itemBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding = BookshelfCellBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: BookHelper) {
       GlideHelper.viewBookImage(model.imageUrl, holder.itemBinding.cellBookImage)
    }
}