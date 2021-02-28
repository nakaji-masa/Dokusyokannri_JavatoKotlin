package android.wings.websarva.dokusyokannrijavatokotlin.firebase


import android.wings.websarva.dokusyokannrijavatokotlin.firebase.model.BookHelper
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.model.UserInfoHelper
import android.wings.websarva.dokusyokannrijavatokotlin.notification.Notification
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
    suspend fun getUserInfoFromUid(uid: String): UserInfoHelper? {
        val snapshot = userCollection.document(uid).get().await()
        return snapshot?.toObject(UserInfoHelper::class.java)
    }

    /**
     * 本の情報を取得するメソッドです
     * @param docId ドキュメントID
     * @return 本の情報
     */
    suspend fun getBookDataFromDocId(docId: String): BookHelper? {
        return postCollection.document(docId).get().await().toObject(BookHelper::class.java)
    }

    /**
     * いいねをされている投稿を取得する
     * @return List<BookHelper> 投稿のリスト
     */
    suspend fun getBookDataListOnlyLiked(): List<BookHelper> {
        return postCollection
            .whereEqualTo("uid", AuthHelper.getUid())
            .whereNotEqualTo("likedCount", 0)
            .orderBy("likedCount")
            .get().await().toObjects(BookHelper::class.java)
    }

    /**
     * コメントをされている投稿を取得する
     * @return List<BookHelper> 投稿のリスト
     */
    suspend fun getBookDataListOnlyCommented(): List<BookHelper> {
        return postCollection
            .whereEqualTo("uid", AuthHelper.getUid())
            .whereNotEqualTo("commentedCount", 0)
            .orderBy("commentedCount")
            .get().await().toObjects(BookHelper::class.java)
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
    fun getAllRecyclerOptions(): FirestoreRecyclerOptions<BookHelper> {
        return FirestoreRecyclerOptions.Builder<BookHelper>()
            .setQuery(
                fireStore.collection(COLLECTION_POST_PATH)
                    .orderBy("createdAt", Query.Direction.DESCENDING), BookHelper::class.java
            )
            .build()
    }

    /**
     * 指定したユーザーのほんの情報を取得するメソッド
     * @return FirestoreRecyclerOptions
     */
    fun getRecyclerOptionsFromUid(uid: String): FirestoreRecyclerOptions<BookHelper> {
        return FirestoreRecyclerOptions.Builder<BookHelper>()
            .setQuery(
                fireStore.collection(COLLECTION_POST_PATH)
                    .whereEqualTo("uid", uid)
                    .orderBy("createdAt", Query.Direction.DESCENDING), BookHelper::class.java
            )
            .build()
    }

    /**
     * 本のタイトルから情報を取得するメソッド
     * @param title 本のタイトル
     * @return FirestoreRecyclerOptions
     */
    fun getRecyclerOptionsFromTitle(title: String): FirestoreRecyclerOptions<BookHelper> {
        return FirestoreRecyclerOptions.Builder<BookHelper>()
            .setQuery(
                fireStore.collection(COLLECTION_POST_PATH)
                    .whereEqualTo("title", title)
                    .orderBy("createdAt", Query.Direction.DESCENDING), BookHelper::class.java

            )
            .build()

    }

    /**
     * 本のタイトルから情報を取得するメソッド(部分一致)
     * @param text 検索用文字列
     * @return FirestoreRecyclerOptions
     */
    fun getRecyclerOptionsFromSearchText(text: String): FirestoreRecyclerOptions<BookHelper> {
        val options = FirestoreRecyclerOptions.Builder<BookHelper>()
            .setQuery(
                fireStore.collection(COLLECTION_POST_PATH)
                    .orderBy("title")
                    .startAt(text)
                    .endAt("$text\uf8ff"), BookHelper::class.java
            )
            .build()
        options.snapshots.sortByDescending { it.createdAt }
        return options

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
     * いいねの通知を確認したことを更新するメソッド
     * @param notification
     */
    fun updateLikedNotification(notification: Notification) {
        notification.bookHelper.likedList.last { it.id == notification.id }.checked = true
        savePostData(notification.bookHelper)
    }

    /**
     * コメントの通知を確認したことを更新するメソッド
     * @param notification
     */
    fun updateCommentedNotification(notification: Notification) {
        notification.bookHelper.commentedList.last { it.id == notification.id }.checked = true
        savePostData(notification.bookHelper)
    }

}
