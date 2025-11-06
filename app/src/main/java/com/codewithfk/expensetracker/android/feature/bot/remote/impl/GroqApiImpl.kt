package com.codewithfk.expensetracker.android.feature.bot.remote.impl

import android.util.Log
import com.codewithfk.expensetracker.android.feature.bot.remote.GroqApi
import com.codewithfk.expensetracker.android.feature.bot.remote.dto.GroqRequest
import com.codewithfk.expensetracker.android.feature.bot.remote.dto.GroqResponse
import com.codewithfk.expensetracker.android.feature.bot.remote.dto.Message
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import javax.inject.Inject

class GroqApiImpl @Inject constructor(private val client: HttpClient) : GroqApi {
    override suspend fun getAnswer(question: String): String {
        val systemMessage = Message(
            role = "system",
            content = "You are a friendly and helpful financial assistant. Your answers should be concise and small, easy to understand, and avoid overly formal or robotic language. Do not introduce yourself or mention that you are an AI."
        )
        val userMessage = Message(role = "user", content = question)

        val request = GroqRequest(
            messages = listOf(systemMessage, userMessage),
            model = "llama-3.3-70b-versatile"
        )
        return try {
            val response = client.post("chat/completions") {
                setBody(request)
            }.body<GroqResponse>()

            if (response.error != null) {
                val error = response.error
                val errorMessage = "Groq API Error: ${error.message} (Type: ${error.type}, Code: ${error.code})"
                Log.e("GroqApiImpl", errorMessage)
                return errorMessage
            }

            response.choices?.firstOrNull()?.message?.content ?: "No content in response"
        } catch (e: Exception) {
            Log.e("GroqApiImpl", "Error calling Groq API", e)
            "Error: ${e.message}"
        }
    }
}
