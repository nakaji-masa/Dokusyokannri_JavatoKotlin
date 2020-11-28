package android.wings.websarva.dokusyokannrijavatokotlin.utils

import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide

object GlideHelper {
    fun viewGlide(imagePlace: String, imageView: ImageView) {
        if(imagePlace.contains("content")) {
            val uri = Uri.parse(imagePlace)
            Glide.with(imageView.context).load(uri).into(imageView)
        } else {
            Glide.with(imageView.context).load(imagePlace).into(imageView)
        }
    }
}