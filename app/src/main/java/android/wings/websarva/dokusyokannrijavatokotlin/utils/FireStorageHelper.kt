package android.wings.websarva.dokusyokannrijavatokotlin.utils

import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await

object FireStorageHelper {
    private val storageRef = FirebaseStorage.getInstance().reference

    suspend fun saveImage(bytes: ByteArray) {
        storageRef.child(getRefPath()).putBytes(bytes).await()
    }

    private fun getRefPath() :String {
        return AuthHelper.getUid() + ".jpg"
    }

    suspend fun getDownloadUrl() : String{
        return storageRef.child(getRefPath()).downloadUrl.await().toString()
    }
}