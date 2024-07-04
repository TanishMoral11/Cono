package com.example.cono

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.cono.ui.theme.ColorModelMessage
import com.example.cono.ui.theme.ColorUserMessage

@Composable
fun ChatPage(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel) {
    val chatViewModel: ChatViewModel = viewModel()
    val messages = chatViewModel.messageList

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background // This ensures the background respects the theme
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column {
                MessageList(
                    modifier = Modifier.weight(1f),
                    messageList = messages
                )
                MessageInput(onMessage = { chatViewModel.sendMessage(it) })
            }
            AppHeader(
                onNewChat = { chatViewModel.clearChat() },
                onLogOut = {
                    authViewModel.logOut()
                    navController.navigate("login"){
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                }
            )
        }
    }
}

@Composable
fun MessageList(modifier: Modifier = Modifier, messageList: List<MessageModel>) {
    if (messageList.isEmpty()) {
        Column(
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                modifier = Modifier.size(60.dp),
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Icon",
                tint = Color.Unspecified
            )
            Text(
                text = "Your AI friend for making new connections",
                fontSize = 22.sp,
                color = MaterialTheme.colorScheme.onBackground, // Use theme color
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
        }
    } else {
        LazyColumn(
            modifier = modifier,
            reverseLayout = true
        ) {
            items(messageList.reversed()) { message ->
                MessageRow(messageModel = message)
            }
        }
    }
}

@Composable
fun MessageRow(messageModel: MessageModel) {
    val isModel = messageModel.role == "model"
    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .align(if (isModel) Alignment.BottomStart else Alignment.BottomEnd)
                    .padding(
                        start = if (isModel) 8.dp else 70.dp,
                        end = if (isModel) 70.dp else 8.dp,
                        top = 8.dp,
                        bottom = 8.dp
                    )
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isModel) ColorModelMessage else ColorUserMessage)
                    .padding(16.dp)
            ) {
                Column {
                    SelectionContainer {
                        Text(
                            text = messageModel.message,
                            fontWeight = FontWeight.W500,
                            color = if (isModel) Color.Black else Color.White
                        )
                    }
                    if (messageModel.message != "typing...") {
                        IconButton(
                            onClick = {
                                clipboardManager.setText(androidx.compose.ui.text.AnnotatedString(messageModel.message))
                                Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_content_copy_24),
                                contentDescription = "Copy",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MessageInput(onMessage: (String) -> Unit) {
    var message by remember { mutableStateOf("") }

    Row(
        modifier = Modifier.padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            modifier = Modifier.weight(1f),
            value = message,
            onValueChange = { message = it },
            placeholder = { Text("Start A Conversation...") }
        )

        IconButton(onClick = {
            if (message.isNotEmpty()) {
                onMessage(message)
                message = ""
            }
        }) {
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = "Send"
            )
        }
    }
}

@Composable
fun AppHeader(onNewChat: () -> Unit, onLogOut: () -> Unit) {
    var showLogOutButton by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .alpha(0.5f)
                .blur(radius = 10.dp)
                .background(MaterialTheme.colorScheme.surface)
        )

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SelectionContainer {
                Text(
                    text = "Cono",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 22.sp,
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = onNewChat) {
                Icon(
                    imageVector = Icons.Default.Create,
                    contentDescription = "New Chat",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            IconButton(onClick = { showLogOutButton = !showLogOutButton }) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_profile),
                    contentDescription = "Profile",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            if (showLogOutButton) {
                Button(onClick = onLogOut) {
                    Text("Log Out")
                }
            }
        }
    }
}
