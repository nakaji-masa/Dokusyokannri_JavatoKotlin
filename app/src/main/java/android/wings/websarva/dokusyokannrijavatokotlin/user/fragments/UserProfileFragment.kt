package android.wings.websarva.dokusyokannrijavatokotlin.user.fragments

import android.content.ContentResolver
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import android.wings.websarva.dokusyokannrijavatokotlin.MyApplication
import android.wings.websarva.dokusyokannrijavatokotlin.R
import android.wings.websarva.dokusyokannrijavatokotlin.register.UserInfo
import android.wings.websarva.dokusyokannrijavatokotlin.user.fragments.Base.BaseAuthFragment
import android.wings.websarva.dokusyokannrijavatokotlin.utils.AuthHelper
import android.wings.websarva.dokusyokannrijavatokotlin.utils.FireStorageHelper
import androidx.core.graphics.drawable.RoundedBitmapDrawable
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.BitmapImageViewTarget
import kotlinx.android.synthetic.main.fragment_user_profile.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream


class
UserProfileFragment : BaseAuthFragment(), TextWatcher {

    private lateinit var profileUserName: EditText
    private lateinit var profileIntroduction: EditText
    private lateinit var profileImage: ImageView
    private lateinit var saveButton: Button
    private var imageUri: Uri = getDefaultUri()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user_profile, container, false)
        profileUserName = view.userNameInput
        profileIntroduction = view.userIntroductionInput
        profileImage = view.userImage
        saveButton = view.userInfoSaveButton
        Glide.with(context).load(imageUri).into(profileImage)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        profileUserName.addTextChangedListener(this)

        saveButton.setOnClickListener {
            view.userInfoProgressBar.visibility = View.VISIBLE
            val bitmap = (profileImage.drawable as BitmapDrawable).bitmap
            val baos = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()

            GlobalScope.launch {
                FireStorageHelper.saveImage(data)
                val userInfo =
                    UserInfo(
                        profileUserName.text.toString(),
                        profileIntroduction.text.toString(),
                        FireStorageHelper.getDownloadUrl()
                    )
                userCollection.document(AuthHelper.getUid()).set(userInfo).addOnCompleteListener {
                    if (it.isSuccessful) {
                        moveToMainActivity()
                    } else {
                        Toast.makeText(activity, "登録に失敗しました", Toast.LENGTH_SHORT).show()
                    }
                    view.userInfoProgressBar.visibility = View.INVISIBLE
                }
            }
        }

        view.userImage.setOnClickListener {
            selectImage()
        }

    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (profileUserName.text.isNotBlank()) {
            saveButton.isEnabled = true
        }
    }

    override fun afterTextChanged(s: Editable?) {
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == READ_RC) {
            try {
                data?.data?.let {
                    imageUri = it
                }
                val options = RequestOptions()
                    .fitCenter()
                    .placeholder(R.drawable.user_image_background)
                Glide.with(context).asBitmap().apply(options).load(imageUri)
                    .into(object : BitmapImageViewTarget(profileImage) {
                        override fun setResource(resource: Bitmap?) {
                            val circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(
                                    MyApplication.getAppContext().resources,
                                    resource
                                )
                            circularBitmapDrawable.isCircular = true
                            profileImage.setImageDrawable(circularBitmapDrawable)
                        }
                    })

            } catch (e: Exception) {
                Toast.makeText(context, "エラーが発生しました。", Toast.LENGTH_LONG).show()
            }
        }
    }

    /**
     * ユーザーのギャラリーへ遷移するメソッド
     */
    private fun selectImage() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        startActivityForResult(intent, READ_RC)
    }

    /**
     * デフォルトのUriを返すメソッド
     * @return Uri
     */
    private fun getDefaultUri(): Uri {
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                "android.wings.websarva.dokusyokannrijavatokotlin" + "/drawable" + "/ic_account")
    }

    companion object {
        private const val READ_RC = 1

        @JvmStatic
        fun newInstance() =
            UserProfileFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

}