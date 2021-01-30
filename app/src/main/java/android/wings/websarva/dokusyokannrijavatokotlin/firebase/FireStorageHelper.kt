package android.wings.websarva.dokusyokannrijavatokotlin.firebase

import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

object FireStorageHelper {
    private val storageRef = FirebaseStorage.getInstance().reference

    /**
     * FireStorageに画像を保存するメソッド
     * @param bytes 画像のバイトデータ
     */
    suspend fun saveImage(bytes: ByteArray) {
        storageRef.child(getRefPath()).putBytes(bytes).await()
    }

    /**
     * パスを取得するメソッド
     * @return String
     */
    private fun getRefPath() :String {
        return AuthHelper.getUid() + ".jpg"
    }

    /**
     * 画像のUrlを取得するメソッド
     * @return String Url
     */
    suspend fun getDownloadUrl() : String{
        return storageRef.child(getRefPath()).downloadUrl.await().toString()
    }
}