package com.example.gbot.ui

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.example.gbot.BuildConfig
import com.example.gbot.model.Message
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GbotViewModel : ViewModel() {

    private val openAI = OpenAI(BuildConfig.OPEN_AI_API_KEY)
    private var _messages  = mutableStateListOf(
        Message(
            body = "Ciao Giulia, chiedimi quello che vuoi!",
            isMine = false,
            messageState = MutableStateFlow(MessageState.Success)
        )
    )
    val messages : List<Message>
        get() = _messages
    @OptIn(BetaOpenAI::class)
    private val chatMessages : MutableList<ChatMessage> = mutableListOf()
    var userInput by mutableStateOf("")
    //private var prompt = ""


    fun updateUserInput(input : String) {
        userInput = input
    }

    @OptIn(BetaOpenAI::class)
    fun appendMessageToChat() {
        _messages.add(
            Message(
                body = userInput,
                isMine = true,
                messageState = MutableStateFlow(MessageState.Success)
            )
        )
        chatMessages.add(
            ChatMessage(
                content = userInput,
                role = ChatRole.User,
            )
        )
        //prompt += "\n" + userInput
        userInput = ""
    }

    @OptIn(BetaOpenAI::class)
    private fun sendMessage() : ChatCompletionRequest {
        return ChatCompletionRequest(
            model = ModelId("gpt-3.5-turbo"),
            messages = chatMessages as List<ChatMessage>,
            temperature = 0.7,
            topP = 1.0,
        )
    }

    @OptIn(BetaOpenAI::class)
    fun getResponse() {
        viewModelScope.launch {
            _messages.add(
                Message(
                    body = "",
                    isMine = false,
                    messageState = MutableStateFlow(MessageState.Loading))
            )
            Log.d("MESSAGE STATUS", _messages.last().messageState.value.name)
            try {
                val completion = openAI.chatCompletion(sendMessage())
                completion.choices.map {choice ->
                    Log.d("VIEW MODEL", choice.message!!.content)
                    _messages.last().body += "\n" + choice.message!!.content
                    //prompt += _messages.last().body
                    _messages.last().body = _messages.last().body.trim()
                    _messages.last().messageState.update {
                        MessageState.Success
                    }
                    chatMessages.add(
                        choice.message!!
                    )
                }
                Log.d("MESSAGE STATUS", _messages.last().messageState.value.name)
                _messages.forEach {
                    Log.d("MESSAGES", it.body)
                }

            } catch (exception : Exception) {
                exception.message?.let { Log.e("EXCEPTION", it) }
                _messages.last().messageState.update {
                    MessageState.Error
                }
                Log.d("MESSAGE STATUS", _messages.last().messageState.value.name)
            }
        }
    }

}

enum class MessageState {
    Success,
    Loading,
    Error
}