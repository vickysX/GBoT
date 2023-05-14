package com.example.gbot.ui

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aallam.openai.api.completion.CompletionRequest
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.example.gbot.model.Message
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GbotViewModel : ViewModel() {

    private val openAI = OpenAI("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")
    private var _messages  = mutableStateListOf(
        Message(
            body = "Ciao Giulia, chiedimi quello che vuoi!",
            isMine = false,
            messageState = MutableStateFlow(MessageState.Success)
        )
    )
    val messages : List<Message>
        get() = _messages

    var userInput by mutableStateOf("")
    private var prompt = ""


    fun updateUserInput(input : String) {
        userInput = input
    }

    fun appendMessageToChat() {
        _messages.add(
            Message(
                body = userInput,
                isMine = true,
                messageState = MutableStateFlow(MessageState.Success)
            )
        )
        prompt += "\n" + userInput
        userInput = ""
    }

    private fun sendMessage() : CompletionRequest {
        return CompletionRequest(
            model = ModelId("text-davinci-003"),
            prompt = prompt,
            temperature = 0.9,
            maxTokens = 150,
            topP = 1.0,
            echo = false,
        )
    }

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
                val completion = openAI.completion(sendMessage())

                completion.choices.map {choice ->
                    Log.d("VIEW MODEL", choice.text)
                    _messages.last().body += "\n" + choice.text
                    prompt += _messages.last().body
                    _messages.last().body = _messages.last().body.trim()
                    /*_messages.add(
                        Message(
                            body = choice.text.trim(),
                            isMine = false
                        )
                    )*/
                    //prompt += "\n" + choice.text
                }
                _messages.last().messageState.update {
                    MessageState.Success
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