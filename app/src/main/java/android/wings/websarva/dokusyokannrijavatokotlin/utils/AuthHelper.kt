package android.wings.websarva.dokusyokannrijavatokotlin.utils

import android.app.Application
import android.content.Context
import android.provider.Settings.Secure.getString
import android.wings.websarva.dokusyokannrijavatokotlin.MyApplication
import android.wings.websarva.dokusyokannrijavatokotlin.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

object AuthHelper {
    private val mAuth = FirebaseAuth.getInstance()
    private var googleSignInClient: GoogleSignInClient

    init {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(
                MyApplication.getAppContext().getString(R.string.default_web_client_id)
            ).requestEmail().build()
        googleSignInClient = GoogleSignIn.getClient(MyApplication.getAppContext(), gso)
    }

    fun signOut() {
        mAuth.signOut()
        googleSignInClient.signOut()
    }

    fun getFirebaseAuth(): FirebaseAuth {
        return mAuth
    }

    fun getGoogleSignClient(): GoogleSignInClient {
        return googleSignInClient
    }

    fun getUid(): String{
        return mAuth.uid.toString()
    }

    fun isLogin() :Boolean {
        return mAuth.currentUser != null
    }




}