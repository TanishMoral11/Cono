package com.example.cono

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cono.ui.theme.ColorModelMessage
import com.example.cono.ui.theme.ColorUserMessage
import com.example.cono.ui.theme.Purple80


@Composable
fun ChatPage(modifier: Modifier = Modifier, viewModel: ChatViewModel<*>) {
    Box(modifier = modifier.fillMaxSize()) {
        Column {
            // MessageList now takes up the full height
            MessageList(
                modifier = Modifier.weight(1f),
                messageList = viewModel.messageList
            )
            MessageInput(onMessage = {
                viewModel.sendMessage(it)
            })
        }
        // AppHeader is positioned on top
        AppHeader(
            onNewChat = {
                viewModel.clearChat()
            }
        )
    }
}

// This is for the message list
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
                tint = Purple80,
                painter = painterResource(id = R.drawable.baseline_question_answer_24),
                contentDescription = "Icon"
            )
            Text(
                text = "Your AI friend for making new connections",
                fontSize = 22.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )        }
    } else {
        // This is for the lazy column
        // LazyColumn is used for the list of messages that are displayed in the UI in a scrolling manner that is not visible to the user and only the visible messages are displayed to the user and the user can scroll to see more messages
        LazyColumn(
            // This is for the padding of the lazy column that is displayed in the UI in the chat page
            modifier = modifier,
            // This is for the reverse layout of the lazy column that is displayed in the UI in the chat page
            // Its shows message from the bottom to the top
            reverseLayout = true
        ) {
            items(messageList.reversed()) {
                // Use Of 'it' is that, it is the same as the reverse layout of the lazy column that is displayed in the UI in the chat page
                MessageRow(messageModel = it)
            }
        }
    }
}

@Composable
fun MessageRow(messageModel: MessageModel) {
    // We create isModel because we need to know if the message is from the user or the model.
    val isModel = messageModel.role == "model"
    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {

        // This is for the message box that is displayed in the UI in the chat page
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {

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
                    // Adding SelectionContainer to make the message text selectable
                    SelectionContainer {
                        Text(
                            text = messageModel.message,
                            fontWeight = FontWeight.W500
                        )
                    }
                    // Add copy icon button to copy the message to clipboard
                    // Only show the copy icon if the message is not "typing..."
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
                                contentDescription = "Copy"
                            )
                        }
                    }
                }
            }
        }
    }
}

// This is for the message input field
@Composable
fun MessageInput(onMessage: (String) -> Unit) {

    // This remember is used for the message input field
    var message by remember {
        mutableStateOf("")
    }

    Row(
        modifier = Modifier.padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        OutlinedTextField(
            modifier = Modifier.weight(1f),
            value = message,
            onValueChange = {
                message = it
            },
            placeholder = {Text("Start A Conversation...")}
        )

        IconButton(onClick = {
            // Check that user gives some input or not
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
fun AppHeader(onNewChat: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
    ) {
        // This box acts as the blurred background
        Box(
            modifier = Modifier
                .matchParentSize()
                .alpha(0.5f) // Adjust transparency
                .blur(radius = 10.dp) // Adjust blur intensity
                .background(MaterialTheme.colorScheme.surface)
        )

        // This is the content of the header
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
            IconButton(
                onClick = onNewChat
            ) {
                Icon(
                    imageVector = Icons.Default.Create,
                    contentDescription = "New Chat",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
