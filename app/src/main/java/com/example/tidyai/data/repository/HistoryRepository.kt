package com.example.tidyai.data.repository

import com.example.tidyai.data.local.dao.ClutterDao
import com.example.tidyai.data.local.entity.TidyAnalysisEntity
import com.example.tidyai.data.remote.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

interface HistoryRepository {
    fun getHistory(): Flow<List<TidyAnalysis>>
    suspend fun saveResult(result: TidyAnalysis, imageUri: String)
    suspend fun deleteResult(resultId: Long)
}

@Singleton
class HistoryRepositoryImpl @Inject constructor(
    private val clutterDao: ClutterDao
) : HistoryRepository {

    private val json = Json { ignoreUnknownKeys = true }

    override fun getHistory(): Flow<List<TidyAnalysis>> {
        return clutterDao.getAllHistory().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun saveResult(result: TidyAnalysis, imageUri: String) {
        clutterDao.insertResult(result.toEntity(imageUri))
    }

    override suspend fun deleteResult(resultId: Long) {
        clutterDao.deleteResult(resultId)
    }

    private fun TidyAnalysisEntity.toDomainModel(): TidyAnalysis {
        return TidyAnalysis(
            score = score,
            confidence = confidence,
            roomType = try { json.decodeFromString<RoomType>("\"$roomType\"") } catch (_: Exception) { RoomType.OTHER },
            summary = summary,
            potentialMessage = potentialMessage,
            dimensions = try { json.decodeFromString(dimensionsJson) } catch (_: Exception) { emptyList() },
            zones = try { json.decodeFromString(zonesJson) } catch (_: Exception) { emptyList() },
            issues = try { json.decodeFromString(issuesJson) } catch (_: Exception) { emptyList() },
            suggestions = try { json.decodeFromString(suggestionsJson) } catch (_: Exception) { emptyList() },
            positives = try { json.decodeFromString(positivesJson) } catch (_: Exception) { emptyList() }
        )
    }

    private fun TidyAnalysis.toEntity(imageUri: String): TidyAnalysisEntity {
        return TidyAnalysisEntity(
            timestamp = System.currentTimeMillis(),
            imageUri = imageUri,
            score = score,
            confidence = confidence,
            roomType = roomType.name,
            summary = summary,
            potentialMessage = potentialMessage,
            dimensionsJson = json.encodeToString(dimensions),
            zonesJson = json.encodeToString(zones),
            issuesJson = json.encodeToString(issues),
            suggestionsJson = json.encodeToString(suggestions),
            positivesJson = json.encodeToString(positives)
        )
    }
}
