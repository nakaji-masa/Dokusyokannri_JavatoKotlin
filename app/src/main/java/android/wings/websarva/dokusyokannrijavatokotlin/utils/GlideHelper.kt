package android.wings.websarva.dokusyokannrijavatokotlin.utils


import android.widget.ImageView
import android.wings.websarva.dokusyokannrijavatokotlin.R
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

object GlideHelper {

    /**
     * 本の画像を表示するメソッド
     * @param imagePlace url
     * @param imageView ImageView
     */
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

    /**
     * プロフィールの画像を表示するメソッド
     * @param url 画像のURL
     * @param imageView ImageView
     */
    fun viewUserImage(url: String, imageView: ImageView) {
        val requestOptions = RequestOptions
            .circleCropTransform()
            .error(R.drawable.ic_account)
        Glide.with(imageView.context).load(url).apply(requestOptions)
            .into(imageView)
    }
}