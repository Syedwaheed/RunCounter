package com.edu.core.database.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RunPendingSyncEntity(
     @Embedded(prefix = "run_") val run: RunEntity,
    @PrimaryKey(autoGenerate = false)
    val id: String = run.id,
    val mapPictureByte: ByteArray,
    val userId: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RunPendingSyncEntity

        if (run != other.run) return false
        if (id != other.id) return false
        if (!mapPictureByte.contentEquals(other.mapPictureByte)) return false
        if (userId != other.userId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = run.hashCode()
        result = 31 * result + id.hashCode()
        result = 31 * result + mapPictureByte.contentHashCode()
        result = 31 * result + userId.hashCode()
        return result
    }
}