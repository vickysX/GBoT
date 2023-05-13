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
import kotlinx.coroutines.launch

class GbotViewModel : ViewModel() {

    private val openAI = OpenAI("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")
    private var _messages  = mutableStateListOf(
        Message("Ciao Giulia, chiedimi tutto quello che vuoi", false)
    )
    val messages : List<Message>
        get() = _messages

    var userInput by mutableStateOf("")
    private var prompt = ""

    fun updateUserInput(input : String) {
        userInput = input
    }

    fun appendMessageToChat() {
        _messages.add(Message(userInput, true))
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
            echo = false
        )
    }

    fun getResponse() {
        viewModelScope.launch {
            val completion = openAI.completion(sendMessage())
            completion.choices.map {choice ->
                Log.d("VIEW MODEL", choice.text)
                _messages.add(
                    Message(
                        content = choice.text,
                        isMine = false
                    )
                )
                prompt += "\n" + choice.text
            }
            _messages.forEach {
                Log.d("MESSAGES", it.content)
            }
        }
    }

}