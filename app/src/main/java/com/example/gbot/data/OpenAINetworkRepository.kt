package com.example.gbot.data

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.client.OpenAI

class OpenAINetworkRepository(private val openAI: OpenAI) : OpenAIRepository {
    @BetaOpenAI
    override suspend fun getChatCompletionResponse(
        request: ChatCompletionRequest): ChatCompletion =
        openAI.chatCompletion(request)

}