package android.wings.websarva.dokusyokannrijavatokotlin.utils

import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.tasks.await

object FireStoreHelper {
    private const val COLLECTION_PATH = "user"
    private val fireStore = FirebaseFirestore.getInstance()
    private val collection = fireStore.collection(COLLECTION_PATH)


    //プロフィールは登録しているかのチェック
    suspend fun hasUserDocument(): Boolean{
        return try {
            collection.document(AuthHelper.getUid()).get().await().exists()
        } catch (e: FirebaseFirestoreException) {
            Log.d("Suspend", e.localizedMessage)
            false
        }
    }

    fun getCollection(): CollectionReference {
        return collection
    }
}
