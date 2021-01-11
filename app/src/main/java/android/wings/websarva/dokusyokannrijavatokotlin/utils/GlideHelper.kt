package android.wings.websarva.dokusyokannrijavatokotlin.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import android.wings.websarva.dokusyokannrijavatokotlin.MyApplication
import android.wings.websarva.dokusyokannrijavatokotlin.R
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.BitmapImageViewTarget
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

    fun viewUserImage(bitmap: Bitmap?, imageView: ImageView) {
        val requestOptions = RequestOptions
            .circleCropTransform()
            .error(R.drawable.ic_account)
        Glide.with(imageView.context).load(bitmap).apply(requestOptions)
            .into(imageView)
    }
}