package com.example.gbot.ui

import android.system.Os.getenv
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.aallam.openai.api.completion.CompletionRequest
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.example.gbot.model.Message
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class GbotViewModel : ViewModel() {

    private val openAI = OpenAI(getenv("OPEN_AI_API_KEY"))
    private val messages = mutableListOf<Message>()
    private val _uiState = MutableStateFlow(GbotUiState(
        messages = messages,
    ))
    val uiState : StateFlow<GbotUiState> = _uiState.asStateFlow()
    var userPrompt by mutableStateOf("")

    fun updateUserPrompt(input : String) {
        userPrompt = input
    }
    private fun generatePrompt() : String {
        messages.add(Message(userPrompt, true))
        _uiState.update { currentUiState ->
            currentUiState.copy(
                messages = messages
            )
        }
        var prompt = ""
        messages.map { mex ->
            prompt += mex.content + "\n"
        }
        return prompt
    }

    private fun sendMessage() : CompletionRequest {
        return CompletionRequest(
            model = ModelId("text-curie-001"),
            prompt = generatePrompt(),
            temperature = 0.9,
            maxTokens = 250,
            topP = 1.0,
            stop = listOf("\n")
        )
    }

    suspend fun getMessage() {
        val completion = openAI.completion(sendMessage())
        completion.choices.map {choice ->
            messages.add(
                Message(
                    content = choice.text,
                    isMine = false
                )
            )
        }
        _uiState.update { currentUiState ->
            currentUiState.copy(
                messages = messages
            )
        }
    }

}