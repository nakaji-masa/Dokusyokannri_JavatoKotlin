package android.wings.websarva.dokusyokannrijavatokotlin.utils


import android.widget.ImageView
import android.wings.websarva.dokusyokannrijavatokotlin.R
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

object GlideHelper {

    // 初期状態のURL
    const val defaultImageUrl = "default_image"

    /**
     * 本の画像を表示するメソッド
     * @param imageUrl url
     * @param imageView ImageView
     */
    fun viewBookImage(imageUrl: String?, imageView: ImageView) {
        if (imageUrl == defaultImageUrl) {
            Glide.with(imageView.context).load(
                ContextCompat.getDrawable(
                    imageView.context,
                    R.drawable.ic_book_default_image
                )
            ).into(imageView)
        } else {
            Glide.with(imageView.context).load(imageUrl).into(imageView)
        }
    }

    /**
     * プロフィールの画像を表示するメソッド
     * @param url 画像のURL
     * @param imageView ImageView
     */
    fun viewUserImage(url: String?, imageView: ImageView) {
        val requestOptions = RequestOptions
            .circleCropTransform()
            .error(R.drawable.ic_account)
        Glide.with(imageView.context).load(url).apply(requestOptions)
            .into(imageView)
    }
}