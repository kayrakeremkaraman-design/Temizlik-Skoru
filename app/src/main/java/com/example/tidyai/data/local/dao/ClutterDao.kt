package com.example.tidyai.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.tidyai.data.local.entity.TidyAnalysisEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ClutterDao {
    @Query("SELECT * FROM tidy_analysis_results ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<TidyAnalysisEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResult(result: TidyAnalysisEntity)

    @Query("DELETE FROM tidy_analysis_results WHERE id = :id")
    suspend fun deleteResult(id: Long)

    @Query("DELETE FROM tidy_analysis_results")
    suspend fun clearHistory()
}
