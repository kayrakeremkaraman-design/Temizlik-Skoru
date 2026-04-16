package com.example.tidyai.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ── OpenRouter API Request/Response ──

@Serializable
data class OpenRouterRequest(
    val model: String,
    val messages: List<Message>,
    @SerialName("response_format") val responseFormat: ResponseFormat? = null
)

@Serializable
data class Message(
    val role: String,
    val content: List<ContentPart>
)

@Serializable
sealed class ContentPart {
    @Serializable
    @SerialName("text")
    data class TextPart(val text: String) : ContentPart()

    @Serializable
    @SerialName("image_url")
    data class ImagePart(
        @SerialName("image_url") val imageUrl: ImageUrl
    ) : ContentPart()
}

@Serializable
data class ImageUrl(
    val url: String
)

@Serializable
data class ResponseFormat(
    val type: String
)

@Serializable
data class OpenRouterResponse(
    val choices: List<Choice>? = null,
    val error: OpenRouterError? = null
)

@Serializable
data class Choice(
    val message: ResponseMessage
)

@Serializable
data class ResponseMessage(
    val content: String? = null
)

@Serializable
data class OpenRouterError(
    val message: String
)

// ── Domain Models (spec.md) ──

@Serializable
enum class RoomType {
    @SerialName("bedroom") BEDROOM,
    @SerialName("livingRoom") LIVING_ROOM,
    @SerialName("kitchen") KITCHEN,
    @SerialName("bathroom") BATHROOM,
    @SerialName("office") OFFICE,
    @SerialName("other") OTHER
}

@Serializable
enum class IssueSeverity {
    @SerialName("high") HIGH,
    @SerialName("medium") MEDIUM,
    @SerialName("low") LOW
}

@Serializable
data class ScoreDimension(
    val name: String,
    val value: Int,
    val icon: String? = null
)

@Serializable
data class ScoreZone(
    val area: String,
    val score: Int,
    val note: String
)

@Serializable
data class ScoreIssue(
    val severity: IssueSeverity,
    val text: String,
    val zone: String? = null
)

@Serializable
data class ScoreSuggestion(
    val text: String,
    val gain: Int,
    val effort: String
)

@Serializable
data class TidyAnalysis(
    val score: Int,
    val confidence: Double? = null,
    val roomType: RoomType = RoomType.OTHER,
    val summary: String,
    val potentialMessage: String? = null,
    val dimensions: List<ScoreDimension> = emptyList(),
    val zones: List<ScoreZone> = emptyList(),
    val issues: List<ScoreIssue> = emptyList(),
    val suggestions: List<ScoreSuggestion> = emptyList(),
    val positives: List<String> = emptyList()
)
