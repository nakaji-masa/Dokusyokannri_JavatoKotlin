package android.wings.websarva.dokusyokannrijavatokotlin.user.login.fragments


import android.content.ContentResolver
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.Toast
import android.wings.websarva.dokusyokannrijavatokotlin.databinding.FragmentUserProfileBinding
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.AuthHelper
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.FireStorageHelper
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.FireStoreHelper
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.model.UserInfoHelper
import android.wings.websarva.dokusyokannrijavatokotlin.realm.`object`.UserInfoObject
import android.wings.websarva.dokusyokannrijavatokotlin.realm.manager.RealmManager
import android.wings.websarva.dokusyokannrijavatokotlin.user.login.fragments.base.BaseAuthFragment
import android.wings.websarva.dokusyokannrijavatokotlin.utils.GlideHelper
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import io.realm.Realm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

class UserProfileFragment : BaseAuthFragment(), TextWatcher {

    private lateinit var realm: Realm
    private var imageUri: Uri = getDefaultUri()
    private var editMode = false
    private var supportActionBar: ActionBar? = null
    private var _binding: FragmentUserProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            editMode = it.getBoolean(MODE_KEY)
        }
        realm = RealmManager.getUserRealmInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // デフォルトの画像を表示する
        GlideHelper.viewUserImage(imageUri.toString(), binding.userImage)

        if (editMode) {
            // 戻るボタンの表示
            supportActionBar = (activity as AppCompatActivity).supportActionBar
            supportActionBar?.setDisplayShowHomeEnabled(true)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.show()

            // realm編集する場合、登録されている情報を表示する
            val userInfo =
                realm.where(UserInfoObject::class.java).equalTo("uid", AuthHelper.getUid())
                    .findFirst()
            binding.userNameInput.setText(userInfo?.userName)
            binding.userIntroductionInput.setText(userInfo?.introduction)
            GlideHelper.viewUserImage(userInfo?.imageUrl, binding.userImage)

            // 保存ボタンの活性化
            binding.userInfoSaveButton.isEnabled = true
        }

        binding.userNameInput.addTextChangedListener(this)

        binding.userInfoSaveButton.setOnClickListener {
            // progressBarの表示
            binding.userInfoProgressBar.visibility = View.VISIBLE

            GlobalScope.launch(Dispatchers.Main) {
                // FireStorageに登録
                FireStorageHelper.saveImage(getByteArrayFromUserImage())

                // ユーザー情報
                val userInfo =
                    UserInfoHelper(
                        AuthHelper.getUid(),
                        binding.userNameInput.text.toString(),
                        binding.userIntroductionInput.text.toString(),
                        FireStorageHelper.getDownloadUrl()
                    )

                // realmに編集か登録する
                if (editMode) {
                    updateDataToRealm(userInfo)
                } else {
                    createDataToRealm(userInfo)
                }

                // FireStoreに保存が成功したら次の画面に遷移する
                if (FireStoreHelper.hasSavedUserInfo(userInfo)) {
                    moveNextView()
                } else {
                    Toast.makeText(activity, "登録に失敗しました", Toast.LENGTH_SHORT).show()
                }

                // progressBarの非表示
                binding.userInfoProgressBar.visibility = View.INVISIBLE
            }
        }

        binding.userImage.setOnClickListener {
            selectImage()
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        binding.userInfoSaveButton.isEnabled = binding.userNameInput.text?.isNotBlank()!!
    }

    override fun afterTextChanged(s: Editable?) {
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == READ_RC) {
            try {
                data?.data?.let {
                    imageUri = it
                }
            } catch (e: Exception) {
                Toast.makeText(context, "エラーが発生しました。", Toast.LENGTH_LONG).show()
            }
            GlideHelper.viewUserImage(imageUri.toString(), binding.userImage)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                activity?.supportFragmentManager?.popBackStack()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onDestroy() {
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(false)
            it.setDisplayShowHomeEnabled(false)
            it.hide()
        }
        super.onDestroy()
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
     * ユーザー画像をByteArray型に変換する
     * @return ByteArray型に変換した画像
     */
    private fun getByteArrayFromUserImage(): ByteArray {
        val bitmap: Bitmap = try {
            (binding.userImage.drawable as BitmapDrawable).bitmap

        } catch (e: ClassCastException) {
            Bitmap.createBitmap(
                binding.userImage.drawable.intrinsicWidth,
                binding.userImage.drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
        }

        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        return baos.toByteArray()
    }

    /**
     * デフォルトのUriを返すメソッド
     * @return Uri
     */
    private fun getDefaultUri(): Uri {
        return Uri.parse(
            ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                    "android.wings.websarva.dokusyokannrijavatokotlin" + "/drawable" + "/ic_account"
        )
    }

    /**
     * realmにデータを登録するメソッドです
     * @param userInfoHelper ユーザ情報
     */
    private fun createDataToRealm(userInfoHelper: UserInfoHelper) {
        realm.executeTransaction {
            val userInfoObj = it.createObject(UserInfoObject::class.java, AuthHelper.getUid())
            userInfoObj.userName = userInfoHelper.userName
            userInfoObj.introduction = userInfoHelper.introduction
            userInfoObj.imageUrl = userInfoHelper.userImageUrl
        }
    }

    /**
     * realmのデータを更新するメソッドです
     * @param userInfoHelper ユーザー情報
     */
    private fun updateDataToRealm(userInfoHelper: UserInfoHelper) {
        realm.executeTransaction {
            val userInfoObj =
                it.where(UserInfoObject::class.java).equalTo("uid", AuthHelper.getUid()).findFirst()
            userInfoObj?.userName = userInfoHelper.userName
            userInfoObj?.introduction = userInfoHelper.introduction
            userInfoObj?.imageUrl = userInfoHelper.userImageUrl
        }
    }

    /**
     * 次の画面に遷移するメソッドです
     */
    private fun moveNextView() {
        if (editMode) {
            activity?.supportFragmentManager?.popBackStack()
        } else {
            moveToMainActivity()
        }
    }

    companion object {
        private const val READ_RC = 1
        private const val MODE_KEY = "mode_key"
        const val NEW_MODE = "new_mode"
        const val EDIT_MODE = "edit_mode"

        @JvmStatic
        fun newInstance(mode: String) =
            UserProfileFragment().apply {
                arguments = Bundle().apply {
                    if (mode == EDIT_MODE) {
                        putBoolean(MODE_KEY, true)
                    } else {
                        putBoolean(MODE_KEY, false)
                    }
                }
            }
    }

}