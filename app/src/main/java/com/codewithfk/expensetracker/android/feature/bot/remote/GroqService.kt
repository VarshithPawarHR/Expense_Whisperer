package com.codewithfk.expensetracker.android.feature.bot.remote

import javax.inject.Inject

class GroqService @Inject constructor(private val groqApi: GroqApi) {
    suspend fun getAnswer(question: String): String {
        return groqApi.getAnswer(question)
    }
}
