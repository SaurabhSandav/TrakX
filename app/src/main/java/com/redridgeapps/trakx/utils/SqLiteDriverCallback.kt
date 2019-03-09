package com.redridgeapps.trakx.utils

import androidx.sqlite.db.SupportSQLiteDatabase
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver

class SqLiteDriverCallback(schema: SqlDriver.Schema) : AndroidSqliteDriver.Callback(schema) {

    override fun onOpen(db: SupportSQLiteDatabase) {
        super.onOpen(db)

        db.execSQL("PRAGMA foreign_keys=ON;")
    }
}
