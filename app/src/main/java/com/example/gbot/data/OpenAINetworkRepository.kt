package com.example.gbot.data

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.audio.Transcription
import com.aallam.openai.api.audio.TranscriptionRequest
import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.client.OpenAI
import javax.inject.Inject

class OpenAINetworkRepository @Inject constructor(val openAI: OpenAI) : OpenAIRepository {
    @BetaOpenAI
    override suspend fun getChatCompletionResponse(
        request: ChatCompletionRequest): ChatCompletion =
        openAI.chatCompletion(request)

    @OptIn(BetaOpenAI::class)
    override suspend fun getTranscriptionResponse(
        request: TranscriptionRequest) : Transcription =
        openAI.transcription(request)


}