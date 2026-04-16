package com.example.tidyai.data.local

import androidx.room.TypeConverter
import com.example.tidyai.data.remote.model.ScoreDimension
import com.example.tidyai.data.remote.model.ScoreIssue
import com.example.tidyai.data.remote.model.ScoreSuggestion
import com.example.tidyai.data.remote.model.ScoreZone
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Converters {

    private val json = Json { ignoreUnknownKeys = true }

    // ── List<String> ──
    @TypeConverter
    fun fromStringList(value: List<String>): String = json.encodeToString(value)

    @TypeConverter
    fun toStringList(value: String): List<String> = try {
        json.decodeFromString(value)
    } catch (_: Exception) { emptyList() }

    // ── List<ScoreDimension> ──
    @TypeConverter
    fun fromDimensionList(value: List<ScoreDimension>): String = json.encodeToString(value)

    @TypeConverter
    fun toDimensionList(value: String): List<ScoreDimension> = try {
        json.decodeFromString(value)
    } catch (_: Exception) { emptyList() }

    // ── List<ScoreZone> ──
    @TypeConverter
    fun fromZoneList(value: List<ScoreZone>): String = json.encodeToString(value)

    @TypeConverter
    fun toZoneList(value: String): List<ScoreZone> = try {
        json.decodeFromString(value)
    } catch (_: Exception) { emptyList() }

    // ── List<ScoreIssue> ──
    @TypeConverter
    fun fromIssueList(value: List<ScoreIssue>): String = json.encodeToString(value)

    @TypeConverter
    fun toIssueList(value: String): List<ScoreIssue> = try {
        json.decodeFromString(value)
    } catch (_: Exception) { emptyList() }

    // ── List<ScoreSuggestion> ──
    @TypeConverter
    fun fromSuggestionList(value: List<ScoreSuggestion>): String = json.encodeToString(value)

    @TypeConverter
    fun toSuggestionList(value: String): List<ScoreSuggestion> = try {
        json.decodeFromString(value)
    } catch (_: Exception) { emptyList() }
}
