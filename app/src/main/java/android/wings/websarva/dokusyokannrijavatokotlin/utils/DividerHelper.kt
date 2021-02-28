package android.wings.websarva.dokusyokannrijavatokotlin.utils

import android.content.Context
import android.wings.websarva.dokusyokannrijavatokotlin.MyApplication
import android.wings.websarva.dokusyokannrijavatokotlin.R
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager

object DividerHelper {

    /**
     * 境界線を返すメソッド
     * @param context
     */
    fun createDivider(context: Context): DividerItemDecoration {
       val dividerItemDecoration = DividerItemDecoration(context, LinearLayoutManager(context).orientation)
        ContextCompat.getDrawable(context, R.drawable.divider)?.let {
            dividerItemDecoration.setDrawable(it)
        }
        return dividerItemDecoration
    }
}