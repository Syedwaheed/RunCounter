package com.edu.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.edu.core.database.dao.DeletedRunSyncDao
import com.edu.core.database.dao.GoalDao
import com.edu.core.database.dao.RunDao
import com.edu.core.database.dao.RunPendingSyncDao
import com.edu.core.database.entity.DeletedRunSyncEntity
import com.edu.core.database.entity.GoalEntity
import com.edu.core.database.entity.RunEntity
import com.edu.core.database.entity.RunPendingSyncEntity

@Database(
    entities = [
        RunEntity::class,
        RunPendingSyncEntity::class,
        DeletedRunSyncEntity::class,
        GoalEntity::class,
    ],
    version = 4
)
abstract class RunDatabase: RoomDatabase() {
    abstract val runDao: RunDao
    abstract val runPendingSyncDao: RunPendingSyncDao
    abstract val deletedRunSyncDao: DeletedRunSyncDao

    abstract val goalDao: GoalDao

}