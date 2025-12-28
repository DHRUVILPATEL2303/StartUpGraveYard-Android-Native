package com.startup.graveyard.presentation.screens.chatscreen


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.startup.graveyard.presentation.models.ChatItemUI
import com.startup.graveyard.presentation.viewmodels.chat.ChatViewModel
import com.startup.graveyard.utils.MessageUI
import com.startup.graveyard.utils.chatKey


@Composable
fun ChatScreen(
    selfId: String,
    peerId: String,
    viewModel: ChatViewModel,
    onBack: () -> Unit
) {
    val chatKey = chatKey(selfId, peerId)
    val messages = viewModel.messages(chatKey)



    DisposableEffect(Unit) {
        onDispose {
            viewModel.disconnect()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = null)
            }
            Text(
                text = peerId,
                fontWeight = FontWeight.Bold
            )
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(12.dp)
        ) {
            items(
                items = messages,
                key = { it.localId }
            ) { msg ->
                MessageBubble(
                    message = msg,
                    isMine = msg.senderId == selfId
                )
            }
        }

        MessageInput {
            viewModel.send(selfId, peerId, it)
        }
    }
}

@Composable
fun MessageBubble(
    message: MessageUI,
    isMine: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isMine)
            Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .padding(vertical = 4.dp)
                .background(
                    color = if (isMine)
                        Color(0xFFDCF8C6)
                    else Color.White,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(12.dp)
                .widthIn(max = 280.dp)
        ) {
            Text(
                text = message.content,
                color = Color.Black
            )
        }
    }
}
@Composable
fun MessageInput(
    onSend: (String) -> Unit
) {
    var text by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color.White),
        verticalAlignment = Alignment.CenterVertically
    ) {

        TextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier.weight(1f),
            placeholder = { Text("Type a message") }
        )

        IconButton(
            onClick = {
                if (text.isNotBlank()) {
                    onSend(text)
                    text = ""
                }
            }
        ) {
            Icon(Icons.Default.Send, contentDescription = "Send")
        }
    }
}