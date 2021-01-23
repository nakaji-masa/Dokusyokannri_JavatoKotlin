package android.wings.websarva.dokusyokannrijavatokotlin.utils


import android.wings.websarva.dokusyokannrijavatokotlin.register.BookHelper
import android.wings.websarva.dokusyokannrijavatokotlin.register.UserInfo
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.*
import kotlinx.coroutines.tasks.await

object FireStoreHelper {
    private const val COLLECTION_USER_PATH = "user"
    private const val COLLECTION_POST_PATH = "post"
    private val fireStore = FirebaseFirestore.getInstance()
    private val userCollection = fireStore.collection(COLLECTION_USER_PATH)
    private val postCollection = fireStore.collection(COLLECTION_POST_PATH)


    /**
     * プロフィール情報があるか判定するメソッド
     * @return Boolean
     */
    suspend fun hasUserDocument(): Boolean {
        return try {
            fireStore.collection(COLLECTION_USER_PATH).document(AuthHelper.getUid()).get().await()
                .exists()
        } catch (e: FirebaseFirestoreException) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Userコレクションからユーザー情報を取得する
     * @return UserInfo ユーザー情報
     */
    suspend fun getUserData(uid: String): UserInfo? {
        val snapshot = userCollection.document(uid).get().await()
        return snapshot?.toObject(UserInfo::class.java)
    }

    /**
     * FireStoreに本の情報を保存する
     * @param data 保存するデータ
     */
    fun savePostData(data: BookHelper) {
        postCollection.document(data.docId).set(data)
    }

    /**
     * RecyclerOptionsを取得するメソッド
     * @return FirestoreRecyclerOptions
     */
    fun getRecyclerOptions(): FirestoreRecyclerOptions<BookHelper> {
        return FirestoreRecyclerOptions.Builder<BookHelper>()
            .setQuery(
                fireStore.collection(COLLECTION_POST_PATH)
                    .whereNotEqualTo("uid", AuthHelper.getUid())
                    .orderBy("uid")
                    .orderBy("createdAt", Query.Direction.DESCENDING), BookHelper::class.java
            )
            .build()
    }

}
