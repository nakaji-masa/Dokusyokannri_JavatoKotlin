package android.wings.websarva.dokusyokannrijavatokotlin.utils

import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await

object FireStorageHelper {
    private val storageRef = FirebaseStorage.getInstance().reference

    fun getUserImageRef() : StorageReference {
        return storageRef.child(getRefPath())
    }

    fun getStorageReference(): StorageReference {
        return storageRef
    }

    suspend fun getImageByteArray(fileName: String): ByteArray {
        return storageRef.child(fileName).getBytes(1024 * 1024).await()
    }

    fun getRefPath() :String {
        return  AuthHelper.getUid() + ".jpg"
    }
}