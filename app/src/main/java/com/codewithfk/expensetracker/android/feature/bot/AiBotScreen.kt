package com.codewithfk.expensetracker.android.feature.bot

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.codewithfk.expensetracker.android.R
import com.codewithfk.expensetracker.android.ui.theme.Zinc

@Composable
fun AiBotScreen(viewModel: AiBotViewModel = hiltViewModel()) {
    val summary by viewModel.transactionSummary.collectAsState()
    val messages by viewModel.messages.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var question by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        SummaryCard(summary = summary)

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            reverseLayout = true
        ) {
            items(messages.reversed()) {
                ChatBubble(message = it)
            }
        }

        ChatInput(question, { question = it }, isLoading) {
            if (question.isNotEmpty()) {
                viewModel.onEvent(AiBotEvent.OnAskQuestion(question))
                question = ""
            }
        }
    }
}

@Composable
fun SummaryCard(summary: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Transaction Summary", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = summary)
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = if (message.isFromUser) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(if (message.isFromUser) Zinc else Color.LightGray)
                .padding(16.dp)
        ) {
            Text(text = message.text, color = if (message.isFromUser) Color.White else Color.Black)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatInput(
    question: String,
    onQuestionChange: (String) -> Unit,
    isLoading: Boolean,
    onSend: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = question,
            onValueChange = onQuestionChange,
            modifier = Modifier.weight(1f),
            placeholder = { Text("Ask a question...") },
            enabled = !isLoading,
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(onClick = onSend, enabled = !isLoading) {
            Icon(painter = painterResource(id = R.drawable.ic_send), contentDescription = "Send")
        }
    }
}
