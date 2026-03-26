package com.edu.core.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val Migration4To5 = object : Migration(4, 5) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Add userId column to GoalEntity
        // Existing goals will have empty string as userId (orphaned)
        // This means they won't show for any user - clean slate for new architecture
        db.execSQL("ALTER TABLE GoalEntity ADD COLUMN userId TEXT NOT NULL DEFAULT ''")
    }
}