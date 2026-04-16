package com.example.tidyai.di

import android.content.Context
import androidx.room.Room
import com.example.tidyai.data.local.TidyDatabase
import com.example.tidyai.data.local.dao.ClutterDao
import com.example.tidyai.data.remote.OpenRouterApi
import com.example.tidyai.data.repository.HistoryRepository
import com.example.tidyai.data.repository.HistoryRepositoryImpl
import com.example.tidyai.data.repository.SettingsRepository
import com.example.tidyai.data.repository.SettingsRepositoryImpl
import com.example.tidyai.data.repository.TidyAiRepository
import com.example.tidyai.data.repository.TidyAiRepositoryImpl
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideOpenRouterApi(client: OkHttpClient): OpenRouterApi {
        val json = Json { 
            ignoreUnknownKeys = true 
            coerceInputValues = true
        }
        val contentType = "application/json".toMediaType()
        
        return Retrofit.Builder()
            .baseUrl("https://openrouter.ai/api/v1/")
            .client(client)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
            .create(OpenRouterApi::class.java)
    }

    @Provides
    @Singleton
    fun provideTidyAiRepository(api: OpenRouterApi): TidyAiRepository {
        return TidyAiRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideTidyDatabase(@ApplicationContext context: Context): TidyDatabase {
        return Room.databaseBuilder(
            context,
            TidyDatabase::class.java,
            "tidy_database"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideClutterDao(database: TidyDatabase): ClutterDao {
        return database.clutterDao()
    }

    @Provides
    @Singleton
    fun provideHistoryRepository(clutterDao: ClutterDao): HistoryRepository {
        return HistoryRepositoryImpl(clutterDao)
    }

    @Provides
    @Singleton
    fun provideSettingsRepository(@ApplicationContext context: Context): SettingsRepository {
        return SettingsRepositoryImpl(context)
    }
}
