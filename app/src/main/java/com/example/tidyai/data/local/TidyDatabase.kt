package com.example.tidyai.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.tidyai.data.local.dao.ClutterDao
import com.example.tidyai.data.local.entity.TidyAnalysisEntity

@Database(entities = [TidyAnalysisEntity::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class TidyDatabase : RoomDatabase() {
    abstract fun clutterDao(): ClutterDao
}
