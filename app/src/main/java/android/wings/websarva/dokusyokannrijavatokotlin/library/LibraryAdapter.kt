package android.wings.websarva.dokusyokannrijavatokotlin.library

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.AsyncTask
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.wings.websarva.dokusyokannrijavatokotlin.R
import android.wings.websarva.dokusyokannrijavatokotlin.activities.RegisterActivity
import android.wings.websarva.dokusyokannrijavatokotlin.register.BookHelper
import android.wings.websarva.dokusyokannrijavatokotlin.utils.GlideHelper
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import kotlinx.android.synthetic.main.activity_register.*
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class LibraryAdapter(options: FirestoreRecyclerOptions<BookHelper>) :
    FirestoreRecyclerAdapter<BookHelper, LibraryAdapter.ViewHolder>(options) {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        //val bookImageCell: ImageView = view.findViewById(R.id.bookImageUsersCell)
        //val bookDateCell: TextView = view.findViewById(R.id.bookDateUsersActuallyTextViewCell)
        //val bookTitleCell: TextView = view.findViewById(R.id.bookTitleUsersActuallyTextViewCell)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.users_book_list_cell, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: BookHelper) {
       // holder.bookTitleCell.text = model.title
        //holder.bookDateCell.text = model.date
        //GlideHelper.viewGlide(model.image, holder.bookImageCell)
    }

}