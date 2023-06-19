package com.example.gbot.data

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.audio.Transcription
import com.aallam.openai.api.audio.TranscriptionRequest
import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequest

interface OpenAIRepository {

    @OptIn(BetaOpenAI::class)
    suspend fun getChatCompletionResponse(
        request: ChatCompletionRequest
    ) : ChatCompletion

    @OptIn(BetaOpenAI::class)
    suspend fun getTranscriptionResponse(
        request : TranscriptionRequest
    ) : Transcription

}