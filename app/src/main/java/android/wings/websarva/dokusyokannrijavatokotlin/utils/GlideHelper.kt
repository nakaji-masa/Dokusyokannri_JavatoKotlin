package android.wings.websarva.dokusyokannrijavatokotlin.utils

import android.net.Uri
import android.util.Log
import android.widget.ImageView
import android.wings.websarva.dokusyokannrijavatokotlin.R
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.ViewTarget

object GlideHelper {
    fun viewGlide(imagePlace: String, imageView: ImageView) {
        if (imagePlace.isEmpty()) {
            Glide.with(imageView.context).load(
                ContextCompat.getDrawable(
                    imageView.context,
                    R.drawable.ic_book_default_image
                )
            ).into(imageView)
        } else {
            Glide.with(imageView.context).load(imagePlace).into(imageView)
        }
    }
}