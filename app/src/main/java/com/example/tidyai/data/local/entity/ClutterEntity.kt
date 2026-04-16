package com.example.tidyai.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tidy_analysis_results")
data class TidyAnalysisEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long,
    val imageUri: String,
    val score: Int,
    val confidence: Double?,
    val roomType: String,
    val summary: String,
    val potentialMessage: String?,
    val dimensionsJson: String,   // JSON string of List<ScoreDimension>
    val zonesJson: String,        // JSON string of List<ScoreZone>
    val issuesJson: String,       // JSON string of List<ScoreIssue>
    val suggestionsJson: String,  // JSON string of List<ScoreSuggestion>
    val positivesJson: String     // JSON string of List<String>
)
