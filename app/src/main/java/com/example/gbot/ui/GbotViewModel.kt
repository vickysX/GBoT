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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.ZoneId

class GbotViewModel : ViewModel() {

    private val openAI = OpenAI(BuildConfig.OPEN_AI_API_KEY)

    private var _messages  = mutableStateListOf(
        Message(
            content = "Ciao Giulia, chiedimi quello che vuoi!",
            isMine = false,
            messageState = MutableStateFlow(MessageState.Loading),
            timeStamp = LocalTime.now(ZoneId.systemDefault()),
            isAudio = false
        )
    )
    val messages : List<Message>
        get() = _messages

    @OptIn(BetaOpenAI::class)
    private val chatMessages : MutableList<ChatMessage> = mutableListOf(
        ChatMessage(
            content = "Ciao Giulia, chiedimi quello che vuoi!",
            role = ChatRole.System
        )
    )
    var audioMode by mutableStateOf(true)
    var userInput by mutableStateOf("")
    //private var prompt = ""

    init {
        viewModelScope.launch {
            delay(1000L)
            _messages[0].messageState.update {
                MessageState.Success
            }
        }
    }

    fun switchMessageMode() {
        audioMode = !userInput.isNotEmpty()
    }
    fun updateUserInput(input : String) {
        userInput = input
        audioMode = !userInput.isNotEmpty()
    }

    @OptIn(BetaOpenAI::class)
    fun appendMessageToChat() {
        _messages.add(
            Message(
                content = userInput,
                isMine = true,
                messageState = MutableStateFlow(MessageState.Success),
                timeStamp = LocalTime.now(ZoneId.systemDefault()),
                isAudio = false
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
                    content = "",
                    isMine = false,
                    messageState = MutableStateFlow(MessageState.Loading),
                    timeStamp = LocalTime.now(ZoneId.systemDefault()),
                    isAudio = false
                )
            )
            Log.d("MESSAGE STATUS", _messages.last().messageState.value.name)
            try {
                val completion = openAI.chatCompletion(sendMessage())
                completion.choices.map {choice ->
                    Log.d("VIEW MODEL", choice.message!!.content)
                    _messages.last().content += "\n" + choice.message!!.content
                    //prompt += _messages.last().body
                    _messages.last().content = _messages.last().content.trim()
                    _messages.last().timeStamp = LocalTime
                        .now(ZoneId.systemDefault())
                    _messages.last().messageState.update {
                        MessageState.Success
                    }
                    chatMessages.add(
                        choice.message!!
                    )
                }
                Log.d("MESSAGE STATUS", _messages.last().messageState.value.name)
                _messages.forEach {
                    Log.d("MESSAGES", it.content)
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