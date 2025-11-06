package com.codewithfk.expensetracker.android.feature.bot.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class GroqRequest(
    val messages: List<Message>,
    val model: String
)

@Serializable
data class GroqResponse(
    val choices: List<Choice>? = null,
    val error: GroqError? = null
)

@Serializable
data class Choice(
    val message: Message? = null
)

@Serializable
data class Message(
    val role: String? = null,
    val content: String? = null
)

@Serializable
data class GroqError(
    val message: String? = null,
    val type: String? = null,
    val param: String? = null,
    val code: String? = null
)
