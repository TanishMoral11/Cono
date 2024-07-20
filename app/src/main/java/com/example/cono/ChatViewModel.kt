// ChatViewModel.kt
package com.example.cono

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cono.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {

    val messageList by lazy {
        mutableStateListOf<MessageModel>()
    }
    val apiKey = BuildConfig.API_KEY
    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash-001",
        apiKey = apiKey
    )

    fun sendMessage(question: String) {
        viewModelScope.launch {
            try {
                val instruction = "Behave like an actual human and show some excitement, curiosity, kindness, and empathy in your response. Provide a concise response to continue the conversation without extra phrases."

                messageList.add(MessageModel(question, "user"))
                messageList.add(MessageModel("Thinking ...", "model"))

                val chatContent = content {
                    text(instruction)
                    for (message in messageList.dropLast(2)) {
                        text("${message.role}: ${message.message}")
                    }
                    text("user: $question")
                }

                val response = generativeModel.generateContent(chatContent)

                messageList.removeLast()
                messageList.add(MessageModel(response.text ?: "No response", "model"))
            } catch (e: Exception) {
                messageList.removeLast()
                messageList.add(MessageModel("Error: ${e.message}", "model"))
            }
        }
    }

    fun getContinuationSuggestion(conversationContext: String) {
        viewModelScope.launch {
            try {
                val prompt = """
                Based on this conversation, provide a single, natural-sounding sentence or question to continue the chat. Respond as a warm, friendly human would, showing appropriate emotions, curiosity, and empathy. Don't use any introductory phrases or explanations. Keep it concise, using only necessary words, and limit your response to 2-3 sentences.

                Conversation context:
                $conversationContext

                Your response (in a single, natural sentence):
                """

                messageList.add(MessageModel("Thinking...", "model"))

                val response = generativeModel.generateContent(prompt)

                messageList.removeLast()
                messageList.add(MessageModel(response.text ?: "No response", "model"))
            } catch (e: Exception) {
                messageList.add(MessageModel("Error: ${e.message}", "model"))
            }
        }
    }

    fun clearChat() {
        messageList.clear()
    }
}
