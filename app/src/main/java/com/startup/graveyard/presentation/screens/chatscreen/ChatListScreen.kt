package com.startup.graveyard.presentation.screens.chatscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.startup.graveyard.presentation.models.ChatItemUI
import com.startup.graveyard.presentation.viewmodels.chat.ChatViewModel


@Composable
fun ChatListScreen(
    selfId: String,
    viewModel: ChatViewModel,
    onChatClick: (peerId: String) -> Unit
) {
    val chats = viewModel.chatList(selfId)

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(
            items = chats,
            key = { it.peerId }
        ) { chat ->
            ChatRow(chat = chat) {
                onChatClick(chat.peerId)
            }
        }
    }
}

@Composable
fun ChatRow(
    chat: ChatItemUI,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(48.dp)
                .background(Color.Gray, CircleShape)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = chat.peerId,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = chat.lastMessage,
                maxLines = 1,
                color = Color.Gray
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = chat.timestamp.toString(),
                fontSize = 12.sp,
                color = Color.Gray
            )

            if (chat.unreadCount > 0) {
                Box(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .background(Color.Green, CircleShape)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = chat.unreadCount.toString(),
                        color = Color.White,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}