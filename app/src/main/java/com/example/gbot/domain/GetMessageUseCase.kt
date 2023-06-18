package com.example.gbot.domain

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.example.gbot.data.ChatRepository
import com.example.gbot.data.OpenAIRepository
import com.example.gbot.model.ChatMessageWrapper
import com.example.gbot.model.Message
import com.example.gbot.ui.MessageState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import java.io.IOException
import java.time.LocalTime
import java.time.ZoneId

class GetMessageUseCase(
    private val chatRepository : ChatRepository,
    private val openAIRepository: OpenAIRepository,
    private val defaultDispatcher : CoroutineDispatcher = Dispatchers.Default
) {


    @OptIn(BetaOpenAI::class)
    suspend operator fun invoke(request: ChatCompletionRequest) =
        withContext(defaultDispatcher) {
            val message = Message(
                textContent = "",
                isMine = false,
                messageState = MutableStateFlow(MessageState.Loading),
                timestamp = LocalTime.now(ZoneId.systemDefault()),
                isAudio = false,
                chatMessage = ChatMessageWrapper(
                    role = "assistant",
                    content = ""
                )
            )
            chatRepository.addMessage(message)
            /*message.messageState.update {
                MessageState.Loading
            }*/
            val lastMessage = chatRepository.getLastMessage().first()
            try {

                val completion = openAIRepository.getChatCompletionResponse(request)
                completion.choices.map {choice ->
                    lastMessage.textContent += "\n" + choice.message!!.content
                    lastMessage.textContent = lastMessage.textContent.trim()
                }
                message.chatMessage = ChatMessageWrapper(
                    role = "assistant",
                    content = lastMessage.textContent
                )
                lastMessage.timestamp = LocalTime.now(ZoneId.systemDefault())
                lastMessage.messageState.update {
                    MessageState.Success
                }
            } catch (exception : IOException) {
                lastMessage.messageState.update {
                    MessageState.Error
                }
            }
            chatRepository.updateMessage(lastMessage)
        }

}