package android.wings.websarva.dokusyokannrijavatokotlin.user.content.myshelf.fragments.base

import android.os.Bundle
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.AuthHelper
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.model.UserInfoHelper
import android.wings.websarva.dokusyokannrijavatokotlin.realm.`object`.UserInfoObject
import android.wings.websarva.dokusyokannrijavatokotlin.realm.manager.RealmManager
import androidx.fragment.app.Fragment
import io.realm.Realm

open class BaseMyFragment: Fragment() {
    private lateinit var realm: Realm
    lateinit var user: UserInfoHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        realm = RealmManager.getUserRealmInstance()
        val userObject = realm.where(UserInfoObject::class.java).equalTo("uid", AuthHelper.getUid()).findFirst()
        userObject?.let {
            user = copyToUserInfoHelperFromUserInfoObject(it)
        }
    }

    /**
     * realmのインスタンスインスタンスからdataクラスのインスタンスに移し替える
     * @param  user realmのユーザー情報
     * @return dataクラスインスタンス
     */
    private fun copyToUserInfoHelperFromUserInfoObject(user: UserInfoObject): UserInfoHelper {
        return UserInfoHelper(
            user.uid,
            user.userName,
            user.introduction,
            user.imageUrl
        )
    }
}