package android.wings.websarva.dokusyokannrijavatokotlin.utils

import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

object FireStorageHelper {
    private val storageRef = FirebaseStorage.getInstance().reference

    fun getUserImageRef() : StorageReference {
        return storageRef.child(getRefPath())
    }

    fun getRefPath() :String {
        return "user/" + AuthHelper.getUid() + ".jpg"
    }
}