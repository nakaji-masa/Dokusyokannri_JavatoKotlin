package android.wings.websarva.dokusyokannrijavatokotlin.main.navigator

import android.content.Context
import android.os.Bundle
import android.wings.websarva.dokusyokannrijavatokotlin.chart.fragments.ChartSelectFragment
import android.wings.websarva.dokusyokannrijavatokotlin.settings.fragments.SettingsFragment
import android.wings.websarva.dokusyokannrijavatokotlin.user.content.myshelf.fragments.MyUserFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.fragment.FragmentNavigator

@Navigator.Name("fragment")
class MainNavigator(
    private val context: Context,
    private val manager: FragmentManager,
    private val containerId: Int
) : FragmentNavigator(context, manager, containerId) {

    override fun navigate(
        destination: Destination,
        args: Bundle?,
        navOptions: NavOptions?,
        navigatorExtras: Navigator.Extras?
    ): NavDestination? {

        if (manager.isStateSaved) {
            return null
        }

        // クラス名を取得
        var className = destination.className

        // パッケージ名まで取得出来ない場合
        if (className[0] == '.') {
            className = context.packageName + className
        }

        // トランザクションの起動
        val transaction = manager.beginTransaction()

        // 現在のfragment
        val currentFragment = manager.primaryNavigationFragment

        if (currentFragment != null) {
            // 前のfragmentは非表示にする。
            // replace()を使わないのは前のfragmentと重なってしまうから。
            transaction.hide(currentFragment)
        }

        // bottomNavigationViewで指定されいるfragmentのタグを取得
        val tag = destination.id.toString()
        var fragment = manager.findFragmentByTag(tag)

        // 指定されたfragmentが既に生成済みか？
        if (fragment == null) {
            // なければ生成する
            fragment = instantiateFragment(context, manager, className, args)

            // add()でfragmentsに追加される。次回以降は生成しなくてもよい
            transaction.add(containerId, fragment, tag)
        }

        if (fragment !is SettingsFragment && isRecreatedFragment(fragment)) {
            // fragmentsから更新前のfragmentを消す
            transaction.remove(fragment)

            // fragmentを再生成
            fragment = instantiateFragment(context, manager, className, args)

            // 再生成したフラグメントを追加する
            transaction.add(containerId, fragment, tag)

            // フラグを消す
            resetFlag(fragment)

        }

        // 値渡しができそう？
        fragment.arguments = args

        // 指定されたfragmentを表示する
        transaction.show(fragment)

        // 現在のfragmentに指定する。
        transaction.setPrimaryNavigationFragment(fragment)

        // 処理の確定
        transaction.commit()

        return destination
    }

    companion object {

        private var bookshelfFlag = false // BookShelfの再生成フラグ
        private var graphFlag = false // GraphFragmentの再生成フラグ
        private var postFlag = false // PostFragmentの再生成フラグ


        /**
         * SettingsFragment以外の再生成フラグメントフラグを立てる
         */
        fun setBookFlag() {
            bookshelfFlag = true
            graphFlag = true
            postFlag = true
        }

        /**
         * アクションプランが追加されたときに立つフラグ
         * GraphFragmentを再生成させる
         */
        fun setActionFlag() {
            graphFlag = true
        }

        /**
         * パラメーターのフラグメントが再生成されているか判定するメソッド
         * @param fragment 現在のフラグメント
         */
        fun isRecreatedFragment(fragment: Fragment): Boolean {
            return when (fragment) {
                is MyUserFragment -> {
                    bookshelfFlag
                }
                is ChartSelectFragment -> {
                    graphFlag
                }
                else -> {
                    postFlag
                }
            }
        }

        /**
         * 再生成したフラグメントのフラグをリセットするメソッド
         * @param fragment 再生成したフラグメント
         */
        fun resetFlag(fragment: Fragment) {
            when (fragment) {
                is MyUserFragment -> {
                    bookshelfFlag = false
                }
                is ChartSelectFragment -> {
                    graphFlag = false
                }
                else -> {
                    postFlag = false
                }
            }
        }
    }

}