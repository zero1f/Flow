package com.zero.flow.data.local.database


//import androidx.databinding.adapters.Converters
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.zero.flow.data.local.dao.SessionDao
import com.zero.flow.data.local.dao.TaskDao
import com.zero.flow.data.local.entity.SessionEntity
import com.zero.flow.data.local.entity.TaskEntity

@Database(
    entities = [
        SessionEntity::class,
        TaskEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class FlowDatabase : RoomDatabase() {

    abstract fun sessionDao(): SessionDao
    abstract fun taskDao(): TaskDao

    companion object {
        const val DATABASE_NAME = "flow_database"
    }
}
