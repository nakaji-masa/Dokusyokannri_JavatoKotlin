package android.wings.websarva.dokusyokannrijavatokotlin.firebase


import android.wings.websarva.dokusyokannrijavatokotlin.firebase.model.BookHelper
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.model.UserInfoHelper
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
     * @return UserInfoHelper ユーザー情報
     */
    suspend fun getUserData(uid: String): UserInfoHelper? {
        val snapshot = userCollection.document(uid).get().await()
        return snapshot?.toObject(UserInfoHelper::class.java)
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

    /**
     * 指定したユーザーのほんの情報を取得するメソッド
     * @param uid ユーザーID
     * @return FirestoreRecyclerOptions
     */
    fun getUserOfRecyclerOptions(uid: String): FirestoreRecyclerOptions<BookHelper> {
        return FirestoreRecyclerOptions.Builder<BookHelper>()
            .setQuery(
                fireStore.collection(COLLECTION_POST_PATH)
                    .whereEqualTo("uid", uid), BookHelper::class.java
            )
            .build()
    }

    /**
     * 指定されたドキュメントを削除するメソッドです
     * @param docId ドキュメントのid
     */
    suspend fun deleteDocument(docId: String) {
        postCollection.document(docId).delete().await()
    }

    /**
     * ユーザーの情報を保存するメソッドです
     * @param userInfoHelper ユーザーのプロフィール
     * @return Boolean　保存が成功したかどうかの判定
     */
    suspend fun hasSavedUserInfo(userInfoHelper: UserInfoHelper): Boolean {
        return try {
            userCollection.document(AuthHelper.getUid()).set(userInfoHelper).await()
            true
        } catch (e: FirebaseFirestoreException) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 本の情報を取得するメソッドです
     * @param docId ドキュメントID
     * @return 本の情報
     */
    suspend fun getBookData(docId: String): BookHelper? {
        return postCollection.document(docId).get().await().toObject(BookHelper::class.java)
    }

}
