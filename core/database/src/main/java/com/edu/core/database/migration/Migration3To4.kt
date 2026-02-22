package com.edu.core.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val Migration3To4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Add goalId to RunEntity
        db.execSQL("ALTER TABLE RunEntity ADD COLUMN goalId TEXT")
        // Add run_goalId to RunPendingSyncEntity (embedded RunEntity with prefix "run_")
        db.execSQL("ALTER TABLE RunPendingSyncEntity ADD COLUMN run_goalId TEXT")
    }
}
