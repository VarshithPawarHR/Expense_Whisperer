package com.codewithfk.expensetracker.android.feature.bot.remote

interface GroqApi {
    suspend fun getAnswer(question: String): String
}
