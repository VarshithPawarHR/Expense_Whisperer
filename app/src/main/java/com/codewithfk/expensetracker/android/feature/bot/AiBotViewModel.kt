package com.codewithfk.expensetracker.android.feature.bot

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.codewithfk.expensetracker.android.base.BaseViewModel
import com.codewithfk.expensetracker.android.base.UiEvent
import com.codewithfk.expensetracker.android.data.dao.ExpenseDao
import com.codewithfk.expensetracker.android.data.model.ExpenseEntity
import com.codewithfk.expensetracker.android.feature.bot.remote.GroqService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChatMessage(
    val text: String,
    val isFromUser: Boolean
)

@HiltViewModel
class AiBotViewModel @Inject constructor(
    private val dao: ExpenseDao,
    private val groqService: GroqService
) : BaseViewModel() {
    private val _transactionSummary = MutableStateFlow("")
    val transactionSummary: StateFlow<String> = _transactionSummary.asStateFlow()

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        dao.getAllExpense().onEach { expenses ->
            _transactionSummary.value = createSummary(expenses)
        }.launchIn(viewModelScope)
    }

    private fun createSummary(expenses: List<ExpenseEntity>): String {
        var totalExpenses = 0.0
        var totalIncome = 0.0
        expenses.forEach {
            if (it.type == "Expense") {
                totalExpenses += it.amount
            } else {
                totalIncome += it.amount
            }
        }
        val balance = totalIncome - totalExpenses
        return "Total Income: $totalIncome\nTotal Expenses: $totalExpenses\nBalance: $balance"
    }

    override fun onEvent(event: UiEvent) {
        when (event) {
            is AiBotEvent.OnAskQuestion -> {
                val userMessage = ChatMessage(text = event.question, isFromUser = true)
                _messages.value = _messages.value + userMessage
                viewModelScope.launch {
                    _isLoading.value = true
                    try {
                        val questionWithContext = "Based on the following transaction summary:\n${_transactionSummary.value}\n\nQuestion: ${event.question}"
                        val botResponse = groqService.getAnswer(questionWithContext)
                        val botMessage = ChatMessage(text = botResponse, isFromUser = false)
                        _messages.value = _messages.value + botMessage
                    } catch (e: Exception) {
                        Log.e("AiBotViewModel", "Error getting answer from Groq", e)
                        val errorMessage = ChatMessage(text = "Sorry, something went wrong.", isFromUser = false)
                        _messages.value = _messages.value + errorMessage
                    } finally {
                        _isLoading.value = false
                    }
                }
            }
        }
    }
}

sealed class AiBotEvent : UiEvent() {
    data class OnAskQuestion(val question: String) : AiBotEvent()
}
