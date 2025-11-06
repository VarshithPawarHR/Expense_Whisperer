package com.codewithfk.expensetracker.android.di

import com.codewithfk.expensetracker.android.BuildConfig
import com.codewithfk.expensetracker.android.feature.bot.remote.GroqApi
import com.codewithfk.expensetracker.android.feature.bot.remote.impl.GroqApiImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideHttpClient(): HttpClient {
        return HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
            install(Logging) {
                level = LogLevel.ALL
                logger = object : Logger {
                    override fun log(message: String) {
                        android.util.Log.d("KtorLogger", message)
                    }
                }
            }
            defaultRequest {
                url("https://api.groq.com/openai/v1/")
                header("Authorization", "Bearer ${BuildConfig.GROQ_API_KEY}")
                header("Content-Type", "application/json")
            }
        }
    }

    @Provides
    @Singleton
    fun provideGroqApi(client: HttpClient): GroqApi {
        return GroqApiImpl(client)
    }
}
