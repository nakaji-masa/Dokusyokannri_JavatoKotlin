package android.wings.websarva.dokusyokannrijavatokotlin

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


class DataBaseHelper(context: Context?) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION ) {

    override fun onCreate(db: SQLiteDatabase) {
        val sb = StringBuilder()
        sb.append("CREATE TABLE BookList (")
        sb.append("_id INTEGER PRIMARY KEY AUTOINCREMENT, ")
        sb.append("bookName TEXT,")
        sb.append("deadline TEXT,")
        sb.append("bookNotice TEXT,")
        sb.append("bookActionplan TEXT,")
        sb.append("bookImage BLOB,")
        sb.append("bookidCount INTEGER")
        sb.append(");")
        val sql = sb.toString()
        db.execSQL(sql)
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
    }

    companion object {
        private const val DATABASE_NAME = "Book.db"
        private const val DATABASE_VERSION = 1
    }
}
