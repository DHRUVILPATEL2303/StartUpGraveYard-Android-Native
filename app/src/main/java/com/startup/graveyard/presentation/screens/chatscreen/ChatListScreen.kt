package com.startup.graveyard.presentation.screens.chatscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.startup.graveyard.domain.models.ChatSummary
import com.startup.graveyard.presentation.models.ChatItemUI
import com.startup.graveyard.presentation.viewmodels.chat.ChatViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

fun ChatSummary.toUI(): ChatItemUI {
    return ChatItemUI(
        peerId = peerId,
        lastMessage = lastMessage,
        timestamp = timestamp,
        unreadCount = unreadCount
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(
    viewModel: ChatViewModel,
    onChatClick: (String) -> Unit,
    selfId: String
) {
    val chats by viewModel.chatList

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Messages",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                ),
                actions = {
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->

        if (chats.isEmpty()) {
            EmptyChatState(modifier = Modifier.padding(paddingValues))
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(top = 8.dp, bottom = 100.dp)
            ) {
                items(
                    items = chats,
                    key = { it.chatKey }
                ) { chatSummary ->
                    val chat = chatSummary.toUI()
                    ChatRow(
                        chat = chat,
                        onClick = { onChatClick(chat.peerId) }
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(start = 80.dp, end = 16.dp),
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                        thickness = 0.5.dp
                    )
                }
            }
        }
    }
}

@Composable
fun ChatRow(
    chat: ChatItemUI,
    onClick: () -> Unit
) {
    val isUnread = chat.unreadCount > 0

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ChatAvatar(
            peerId = chat.peerId,
            size = 56.dp
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "User ${chat.peerId.take(4)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = formatChatTimestamp(chat.timestamp),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isUnread) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = chat.lastMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isUnread) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = if (isUnread) FontWeight.Medium else FontWeight.Normal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )


            }
        }
    }
}

@Composable
fun ChatAvatar(
    peerId: String,
    size: androidx.compose.ui.unit.Dp
) {
    val color = remember(peerId) {
        val hash = abs(peerId.hashCode())
        val colors = listOf(
            Color(0xFFEF5350), Color(0xFFAB47BC), Color(0xFF5C6BC0),
            Color(0xFF29B6F6), Color(0xFF26A69A), Color(0xFF9CCC65),
            Color(0xFFFFA726), Color(0xFF8D6E63)
        )
        colors[hash % colors.size]
    }

    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(color),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = peerId.take(1).uppercase(),
            color = Color.White,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun EmptyChatState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(MaterialTheme.colorScheme.surfaceContainerHighest, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ChatBubble,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "No messages yet",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Start connecting with sellers to see messages here.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

fun formatChatTimestamp(timestampSeconds: Long): String {
    val date = Date(timestampSeconds * 1000)
    val now = Calendar.getInstance()
    val msgTime = Calendar.getInstance().apply { time = date }

    return when {
        now.get(Calendar.YEAR) == msgTime.get(Calendar.YEAR) &&
                now.get(Calendar.DAY_OF_YEAR) == msgTime.get(Calendar.DAY_OF_YEAR) -> {
            SimpleDateFormat("h:mm a", Locale.getDefault()).format(date)
        }
        now.get(Calendar.YEAR) == msgTime.get(Calendar.YEAR) &&
                now.get(Calendar.DAY_OF_YEAR) - msgTime.get(Calendar.DAY_OF_YEAR) == 1 -> {
            "Yesterday"
        }
        else -> {
            SimpleDateFormat("MMM d", Locale.getDefault()).format(date)
        }
    }
}