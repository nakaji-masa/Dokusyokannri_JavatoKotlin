package android.wings.websarva.dokusyokannrijavatokotlin.firebase

import android.wings.websarva.dokusyokannrijavatokotlin.MyApplication
import android.wings.websarva.dokusyokannrijavatokotlin.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

/**
 * TODO:エラーハンドリングの追加
 */
object AuthHelper {
    private val mAuth = FirebaseAuth.getInstance()

    /**
     * ログアウトをするメソッド
     */
    fun signOut() {
        mAuth.signOut()
        getGoogleSignClient().signOut()
    }

    /**
     * 認証情報を返すメソッド
     * @return mAuth 認証情報
     */
    fun getFirebaseAuth(): FirebaseAuth {
        return mAuth
    }

    /**
     * GoogleSignClientを返すメソッド
     * @return GoogleSignClient
     */
    fun getGoogleSignClient(): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(
                MyApplication.getAppContext().getString(R.string.default_web_client_id)
            ).requestEmail().build()
        return GoogleSignIn.getClient(MyApplication.getAppContext(), gso)
    }

    /**
     * uidを返すメソッド
     * @return String uid
     */
    fun getUid(): String{
        return mAuth.uid.toString()
    }

    /**
     * ログインしているかを判定するメソッド
     * @return Boolean
     */
    fun isLogin() :Boolean {
        return mAuth.currentUser != null
    }
}