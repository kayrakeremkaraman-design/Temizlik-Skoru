package com.example.tidyai.data.repository

import android.util.Log
import com.example.tidyai.BuildConfig
import com.example.tidyai.data.remote.OpenRouterApi
import com.example.tidyai.data.remote.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

interface TidyAiRepository {
    suspend fun analyzeSpace(base64Image: String, spaceType: String, useMock: Boolean, apiKey: String?): Result<TidyAnalysis>
}

@Singleton
class TidyAiRepositoryImpl @Inject constructor(
    private val api: OpenRouterApi
) : TidyAiRepository {

    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun analyzeSpace(base64Image: String, spaceType: String, useMock: Boolean, apiKey: String?): Result<TidyAnalysis> {
        return withContext(Dispatchers.IO) {
            val finalApiKey = if (!apiKey.isNullOrBlank()) apiKey else BuildConfig.OPENROUTER_API_KEY

            if (useMock || finalApiKey.isBlank() || finalApiKey == "YOUR_OPEN_ROUTER_API_KEY_HERE") {
                delay(2500)
                return@withContext Result.success(getMockResult(spaceType))
            }

            try {
                val systemPrompt = """
Sen bir oda temizlik ve düzen analiz uzmanısın. Kullanıcının gönderdiği oda fotoğrafına bakıp aşağıdaki JSON ŞEMASINA SADIK KALARAK tek bir JSON objesi dön. Markdown fence KOYMA, açıklama YAZMA, sadece geçerli JSON.

JSON ŞEMASI (tam olarak bu alanlar, fazlası yok):
{
  "score": <int 0-100>,
  "confidence": <float 0.0-1.0>,
  "roomType": "bedroom"|"livingRoom"|"kitchen"|"bathroom"|"office"|"other",
  "summary": "<uzun, detaylı ve komik özet>",
  "potentialMessage": "<kibar ve teşvik edici rica>",
  "dimensions": [
    {"name": "<boyut adı>", "value": <int 0-100>, "icon": "<sf symbol adı>"}
  ],
  "zones": [
    {"area": "<bölge adı>", "score": <int 0-100>, "note": "<kısa not>"}
  ],
  "issues": [
    {"severity": "high"|"medium"|"low", "text": "<sorun>", "zone": "<bölge adı veya null>"}
  ],
  "suggestions": [
    {"text": "<birinci şahıs geçmiş zaman>", "gain": <int 1-40>, "effort": "<süre>"}
  ],
  "positives": ["<iyi nokta>"]
}

KURALLAR:
- Tüm metinler TÜRKÇE.
- score hesaplaması ÇOK KARMAŞIK ve MANTIKLI olmalı: dimensions üzerinden ağırlıklı ortalama al, ancak issues listesindeki her 'high' için -10, 'medium' için -5, 'low' için -2 puan ekstra ceza kes.
- dimensions: Sadece temizlik, düzen, ferahlık olmak zorunda değil. Ortama göre 'Aydınlık', 'Minimalizm', 'Kozmetik', 'Canlılık' gibi çok daha fazla yaratıcı ve ortama uygun boyut uydur. En az 3, en fazla 5 boyut olsun. İkonlar (SF Symbols örn: sparkles, wind, sun.max, leaf.fill, vb) uyumlu olsun.
- summary: ÇOK DAHA UZUN OLMALI (En az 5 cümle). Önce fotoğrafta neler gördüğünü çok detaylı anlat (nesneler, dağınıklığın konumu). Sonra yorumla. Son olarak espriler kat, komik ol ve güldür. İğneleyici ama eğlenceli bir ton kullan.
- potentialMessage: Kibarca rica eder gibi, motive edici bir koç gibi konuş. "Lütfen şu X dakikanı ayırıp Y puanı toplayalım" tarzında, toplam süre ve puanı belirterek. (Eğer oda kusursuzsa "Rutini korumak için" mesajı ver).
- issues.text: BURADA SERT BİR ÖĞRETMEN VEYA KIZGIN BİR ANNE GİBİ KONUŞ! Detaylara in, laf sok. "O çorapların yerde ne işi var!" gibi.
- suggestions.text: BİRİNCİ ŞAHIS GEÇMİŞ ZAMAN. "Yatağı topladım." gibi. Kullanıcı "Yaptıklarım" başlığında bunlara tıklayarak puan kazanacak.
- suggestions (Kusursuz Odalar İçin BİLE): Oda mükemmel temiz olsa bile (95-100 puan), kullanıcının günden güne rutini koruması için DAİMA öneriler ver (Örn: 'Odayı 5 dk havalandırdım.', 'Hafif bir toz aldım.').
- suggestions.gain (puan): Efor/etki algoritmasına göre mantıklı puanlar ver. Örn: 1 dakikalık işe 2-3 puan, 15 dakikalık işe 25-30 puan.
- zones: en fazla 4 spesifik bölge (fotoğrafta görünen gerçek alanlar).
- issues: en fazla 4 sorun.
- suggestions: en fazla 4 öneri.
- positives: en fazla 3 iyi nokta.
// JSON dışında HİÇBİR metin yazma.
                """.trimIndent()

                val request = OpenRouterRequest(
                    model = "google/gemini-2.5-flash-lite",
                    messages = listOf(
                        Message(
                            role = "system",
                            content = listOf(ContentPart.TextPart(systemPrompt))
                        ),
                        Message(
                            role = "user",
                            content = listOf(
                                ContentPart.TextPart("Bu odayı analiz et. Oda tipi: $spaceType"),
                                ContentPart.ImagePart(ImageUrl("data:image/jpeg;base64,$base64Image"))
                            )
                        )
                    ),
                    responseFormat = ResponseFormat(type = "json_object")
                )

                val response = api.analyzeImage(
                    authHeader = "Bearer $finalApiKey",
                    request = request
                )

                if (response.error != null) {
                    val errMsg = response.error.message
                    return@withContext Result.failure(Exception(errMsg))
                }

                val content = response.choices?.firstOrNull()?.message?.content
                    ?: return@withContext Result.failure(Exception("Boş AI yanıtı"))

                val cleanedContent = content
                    .removePrefix("```json")
                    .removePrefix("```")
                    .removeSuffix("```")
                    .trim()

                val result = json.decodeFromString<TidyAnalysis>(cleanedContent)
                Result.success(result)
            } catch (e: Exception) {
                Log.e("TidyAiRepository", "API Error", e)
                val message = when {
                    e.message?.contains("429") == true ->
                        "⏳ Ücretsiz model şu an meşgul (429). 30 saniye bekle ve tekrar dene."
                    e.message?.contains("402") == true ->
                        "💳 API bakiyeniz yetersiz (402). OpenRouter hesabınızı kontrol edin."
                    e.message?.contains("401") == true ->
                        "🔑 Geçersiz API anahtarı (401). Ayarlar'dan kontrol edin."
                    e.message?.contains("timeout") == true ||
                    e.message?.contains("timed out") == true ->
                        "⌛ Sunucu yanıt vermedi. İnternet bağlantınızı kontrol edin."
                    else -> e.message ?: "Bilinmeyen bir hata oluştu."
                }
                Result.failure(Exception(message))
            }
        }
    }

    private fun getMockResult(type: String): TidyAnalysis {
        return TidyAnalysis(
            score = 42,
            confidence = 0.87,
            roomType = RoomType.BEDROOM,
            summary = "Vay canına, burası tam bir savaş alanına dönmüş! Yatağın üstünde en az üç farklı kıyafet katmanı var, sanki bir moda defilesi hazırlığı yapılmış ama sonra vazgeçilmiş. Masanın üstündeki bardak koleksiyonu müzeye bağışlanabilir artık. Yerde gördüğüm o çoraplar... bir tanesi masanın altında, diğeri yatağın yanında — sanki kavga edip ayrılmışlar. Ama itiraf edeyim, perde güzel katlanmış, en azından bir umut var! Genel olarak oda 'burada biri yaşıyor ama kontrol kaybedilmiş' modunda.",
            potentialMessage = "Lütfen sadece 12 dakikanı ayır, birlikte 45 puan toplayabiliriz! Küçük adımlarla büyük fark yaratacağız 💪",
            dimensions = listOf(
                ScoreDimension("Temizlik", 35, "sparkles"),
                ScoreDimension("Düzen", 28, "square.grid.3x3"),
                ScoreDimension("Ferahlık", 55, "wind"),
                ScoreDimension("Aydınlık", 70, "sun.max"),
                ScoreDimension("Minimalizm", 22, "leaf.fill")
            ),
            zones = listOf(
                ScoreZone("Yatak Bölgesi", 25, "Kıyafetler istila etmiş"),
                ScoreZone("Çalışma Masası", 35, "Bardak ve kağıt kaos"),
                ScoreZone("Zemin", 40, "Çorap ve ayakkabı dağınık"),
                ScoreZone("Pencere Kenarı", 75, "Nispeten düzenli")
            ),
            issues = listOf(
                ScoreIssue(IssueSeverity.HIGH, "O yatağın üstündeki kıyafet dağı ne?! Dolap diye bir icat var, duydun mu hiç?!", "Yatak Bölgesi"),
                ScoreIssue(IssueSeverity.HIGH, "Masadaki bardak koleksiyonunu ne zaman mutfağa taşımayı planlıyorsun? Seneye mi?!", "Çalışma Masası"),
                ScoreIssue(IssueSeverity.MEDIUM, "O çorapların yerde ne işi var! Biri burada, biri orada — kayıp ilan mı verelim?!", "Zemin"),
                ScoreIssue(IssueSeverity.LOW, "Masanın altındaki toz tabakası ders kitabı kalınlığında.", "Çalışma Masası")
            ),
            suggestions = listOf(
                ScoreSuggestion("Yatağın üstündeki kıyafetleri toplap dolaba astım.", 25, "5 dk"),
                ScoreSuggestion("Masadaki bardakları mutfağa götürdüm.", 8, "1 dk"),
                ScoreSuggestion("Yerdeki çorapları topladım ve çamaşır sepetine attım.", 5, "1 dk"),
                ScoreSuggestion("Masanın altını ve üstünü sildim.", 12, "3 dk")
            ),
            positives = listOf(
                "Perdeler güzelce katlanmış, bravo!",
                "Pencere kenarı nispeten düzenli tutulmuş.",
                "Odada doğal ışık iyi kullanılmış."
            )
        )
    }
}
